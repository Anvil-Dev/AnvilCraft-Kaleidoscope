import requests
import json
import base64
import concurrent.futures


def get_profile(player_name: str):
    print(f"Processing: {player_name}")
    proxies = {
        "http": "http://127.0.0.1:7890",
        "https": "http://127.0.0.1:7890"
    }
    # 获取UUID
    uuid_url = f"https://api.mojang.com/users/profiles/minecraft/{player_name}"
    uuid_response = requests.get(uuid_url, proxies=proxies).json()
    # 获取玩家资料
    profile_url = f"https://sessionserver.mojang.com/session/minecraft/profile/{uuid_response['id']}"
    profile_response = requests.get(profile_url, proxies=proxies).json()
    # 检查错误
    if 'errorMessage' in profile_response.keys():
        print(profile_response)
        return
    # 获取玩家名称
    profile_name = profile_response['name']
    for profile_property in profile_response['properties']:
        # 检查属性名称
        if profile_property['name'] != 'textures':
            continue
        property_value = json.loads(base64.b64decode(profile_property['value']))
        skin = property_value['textures']['SKIN']
        # 获取皮肤URL和模型
        texture_url = skin['url']
        texture_model = 'steve'
        if skin.keys().__contains__('metadata'):
            texture_model = skin['metadata']['model']
        # 获取皮肤图片
        print(f"Get texture: {profile_name}")
        texture_response = requests.get(texture_url, proxies=proxies)
        this_contributor_type = 'supporter' if profile_name in supporters else 'contributor'
        this_contributor_key = f'geometry.anvilcraft.{this_contributor_type}.{profile_name.lower()}'
        with open(f'textures/{this_contributor_key}.png', 'wb') as this_file:
            this_file.write(texture_response.content)
        model_str = slim_str if texture_model == 'slim' else steve_str
        model_str = model_str.replace('${{identifier}}', this_contributor_key)
        if this_contributor_key not in custom_dolls:
            custom_dolls.append(this_contributor_key)
        with open(f'models/{this_contributor_key}.json', 'w') as this_file:
            this_file.write(json.dumps(json.loads(model_str), ensure_ascii=False, indent=2))


if __name__ == "__main__":
    with open("contributors.txt", "r") as f:
        lines = f.read().splitlines()
    with open("supporters.txt", "r") as f:
        supporters = f.read().splitlines()
    with open("custom_dolls.json", "r") as f:
        custom_dolls = json.loads(f.read())

    names = []
    name_map = {}
    for line in lines:
        name, mcid = line.split("\t")
        contributor_type = 'supporter' if mcid in supporters else 'contributor'
        key = f'geometry.anvilcraft.{contributor_type}.{mcid.lower()}'
        if key in custom_dolls:
            continue
        names.append(mcid)
        name_map[mcid] = name
    en_us = {}
    zh_cn = {}

    for name in names:
        contributor_type = 'supporter' if name in supporters else 'contributor'
        key = f'geometry.anvilcraft.{contributor_type}.{name.lower()}'
        en_us[key] = name
        zh_cn[key] = f'{name_map[name]}玩偶'

    with open('lang/zh_cn.json', 'w') as file:
        file.write(json.dumps(zh_cn, ensure_ascii=False, indent=2))
    with open('lang/en_us.json', 'w') as file:
        file.write(json.dumps(en_us, ensure_ascii=False, indent=2))

    with open("model_temp/slim.json", "r") as f:
        slim_str = f.read()
    with open("model_temp/steve.json", "r") as f:
        steve_str = f.read()

    # Processing
    with concurrent.futures.ThreadPoolExecutor(max_workers=16) as executor:
        results = executor.map(get_profile, names)

    with open("custom_dolls.json", "w") as f:
        f.write(json.dumps(custom_dolls, ensure_ascii=False, indent=2))

    with open("lang/zh_cn.json", "w") as f:
            f.write(json.dumps(zh_cn, ensure_ascii=False, indent=2))

    with open("lang/en_us.json", "w") as f:
        f.write(json.dumps(en_us, ensure_ascii=False, indent=2))

    print(f"Processed all {len(custom_dolls)} custom dolls")
