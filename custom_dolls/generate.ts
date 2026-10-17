// 从白名单 API 拉取玩家名单，用 model_temp 模板生成自定义玩偶资源。
// 依赖 Node 24+ 原生 TypeScript 类型擦除运行：node custom_dolls/generate.ts
import { readFileSync, readdirSync, writeFileSync } from "node:fs"
import { join } from "node:path"

const API_URL = "https://api.anvilcraft.dev/api/v1/whitelist"
const SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile"
const TYPES = ["plan", "developer", "contributor", "supporter", "mascot"]

const repoRoot = join(import.meta.dirname, "..")
const templateDir = join(import.meta.dirname, "model_temp")
const assetsDir = join(
  repoRoot,
  "src",
  "main",
  "resources",
  "assets",
  "anvilcraft_kaleidoscope",
  "custom_dolls"
)
// 游戏内注册清单：ServerCustomDollResourceLoader 读取 <namespace>:custom_dolls.json
// 并把条目并入 ServerCustomDollLoader 的模型集合，未登记的玩偶不会出现在创造模式标签页
const manifestPath = join(
  repoRoot,
  "src",
  "main",
  "resources",
  "data",
  "kaleidoscope_dolls",
  "custom_dolls.json"
)

interface Player {
  name: string
  uuid: string
  type?: string
  nickname?: string
}

interface Doll {
  player: Player
  key: string
  model: unknown
  texture: Buffer
}

function normalize(name: string): string {
  return name.toLowerCase().replace(/_/g, "")
}

function readJson(path: string): Record<string, string> {
  return JSON.parse(readFileSync(path, "utf8")) as Record<string, string>
}

function readArray(path: string): string[] {
  return JSON.parse(readFileSync(path, "utf8")) as string[]
}

function writeJson(path: string, value: unknown): void {
  writeFileSync(path, JSON.stringify(value, null, 2))
}

async function fetchWithRetry(url: string, label: string): Promise<Response> {
  let lastError: unknown
  for (let attempt = 1; attempt <= 3; attempt++) {
    try {
      const response = await fetch(url)
      if (response.ok) {
        return response
      }
      lastError = new Error(`HTTP ${response.status}`)
      if (response.status !== 429 && response.status < 500) {
        break
      }
    } catch (error) {
      lastError = error
    }
    if (attempt < 3) {
      console.warn(`  第 ${attempt} 次请求失败（${label}）：${lastError}，稍后重试`)
      await new Promise((resolve) => setTimeout(resolve, attempt * 1000))
    }
  }
  throw new Error(`请求 ${url} 失败：${lastError}`)
}

async function fetchPlayers(): Promise<Player[]> {
  const response = await fetchWithRetry(API_URL, "白名单")
  const data = (await response.json()) as { players?: Player[] }
  if (!Array.isArray(data.players)) {
    throw new Error(`白名单响应缺少 players 数组：${JSON.stringify(data)}`)
  }
  for (const player of data.players) {
    if (!player.name || !player.uuid) {
      throw new Error(`白名单条目缺少 name 或 uuid：${JSON.stringify(player)}`)
    }
    if (player.type !== undefined && !TYPES.includes(player.type)) {
      throw new Error(`玩家 ${player.name} 的 type 未知：${player.type}`)
    }
  }
  return data.players
}

function existingNames(): Set<string> {
  const names = new Set<string>()
  for (const sub of ["models", "textures"]) {
    for (const file of readdirSync(join(assetsDir, sub))) {
      names.add(normalize(file.replace(/\.(json|png)$/, "").split(".").pop()!))
    }
  }
  for (const file of ["en_us.json", "zh_cn.json"]) {
    for (const key of Object.keys(readJson(join(assetsDir, "lang", file)))) {
      names.add(normalize(key.split(".").pop()!))
    }
  }
  return names
}

async function prepare(player: Player, key: string): Promise<Doll> {
  const profileResponse = await fetchWithRetry(`${SESSION_URL}/${player.uuid}`, player.name)
  if (profileResponse.status === 204) {
    throw new Error("Mojang 查无此玩家，UUID 可能已失效")
  }
  const profile = (await profileResponse.json()) as {
    properties?: { name: string; value: string }[]
  }
  const textures = profile.properties?.find((property) => property.name === "textures")
  if (!textures) {
    throw new Error("玩家资料不含 textures 属性")
  }
  const decoded = JSON.parse(Buffer.from(textures.value, "base64").toString("utf8")) as {
    textures?: { SKIN?: { url?: string; metadata?: { model?: string } } }
  }
  const skin = decoded.textures?.SKIN
  if (!skin?.url) {
    throw new Error("玩家资料不含 SKIN 贴图")
  }
  const modelName = skin.metadata?.model === "slim" ? "slim" : "steve"

  const textureResponse = await fetchWithRetry(skin.url, `${player.name} 贴图`)
  const texture = Buffer.from(await textureResponse.arrayBuffer())

  const template = readFileSync(join(templateDir, `${modelName}.json`), "utf8")
  if (!template.includes("${{identifier}}")) {
    throw new Error(`模板 ${modelName}.json 不含 ${{identifier}} 占位符`)
  }
  const model = JSON.parse(template.split("${{identifier}}").join(key)) as unknown
  return { player, key, model, texture }
}

async function main(): Promise<void> {
  const players = await fetchPlayers()
  const existing = existingNames()

  const keys = new Map<string, string>()
  const pending: { player: Player; key: string }[] = []
  let skipped = 0
  let withoutType = 0

  for (const player of players) {
    const name = normalize(player.name)
    if (existing.has(name)) {
      skipped++
      continue
    }
    if (!player.type) {
      withoutType++
    }
    const key = `geometry.anvilcraft.${player.type ?? "contributor"}.${player.name.toLowerCase()}`
    const conflict = keys.get(name)
    if (conflict) {
      throw new Error(`玩家 ${player.name} 与 ${conflict} 归一化后同名，键 ${key} 冲突`)
    }
    keys.set(name, player.name)
    pending.push({ player, key })
  }

  console.log(
    `白名单 ${players.length} 人，跳过已有 ${skipped} 人，待生成 ${pending.length} 人`
  )
  if (withoutType > 0) {
    console.log(`其中 ${withoutType} 人白名单未给出 type，按 contributor 归入`)
  }

  // 先取齐全部数据再落盘，避免部分失败时留下模型或贴图残缺的玩偶
  const dolls: Doll[] = []
  const failures: string[] = []
  for (const { player, key } of pending) {
    try {
      dolls.push(await prepare(player, key))
      console.log(`  已获取 ${key}`)
    } catch (error) {
      failures.push(`${player.name}：${error instanceof Error ? error.message : error}`)
    }
  }

  if (failures.length > 0) {
    console.error(`获取失败 ${failures.length} 人，未写入任何文件`)
    for (const failure of failures) {
      console.error(`  失败 ${failure}`)
    }
    process.exitCode = 1
    return
  }

  const enUs = readJson(join(assetsDir, "lang", "en_us.json"))
  const zhCn = readJson(join(assetsDir, "lang", "zh_cn.json"))
  const manifest = readArray(manifestPath)
  for (const doll of dolls) {
    writeJson(join(assetsDir, "models", `${doll.key}.json`), doll.model)
    writeFileSync(join(assetsDir, "textures", `${doll.key}.png`), doll.texture)
    const nickname = doll.player.nickname?.trim()
    enUs[doll.key] = doll.player.name
    zhCn[doll.key] = `${nickname ? nickname : doll.player.name}玩偶`
    manifest.push(doll.key)
    console.log(`  已写入 ${doll.key}`)
  }
  writeJson(join(assetsDir, "lang", "en_us.json"), enUs)
  writeJson(join(assetsDir, "lang", "zh_cn.json"), zhCn)
  writeJson(manifestPath, manifest)

  console.log(`生成 ${dolls.length} 个玩偶，跳过 ${skipped} 个`)
}

await main()
