import argparse
import requests
import json
import pathlib

LIBRARIES = {
    "MaFgLib": "SKI34J7B",
    "FabricAPI": "P7dR8mSH",
    "MaliLib": "GcWjdA9I",
    "YACL": "1eAoo2KR"
}
lib = lambda a, b: {"project_id": LIBRARIES[a], "dependency_type": b}
VERSIONS = {
    "1.21.1": {
        "fabric": [lib("FabricAPI", "required"), lib("YACL", "required"), lib("MaliLib", "optional")],
        "neoforge": [lib("YACL", "required"), lib("MaFgLib", "optional")]
    },
    "1.21.11": {
        "fabric": [lib("FabricAPI", "required"), lib("YACL", "required"), lib("MaliLib", "optional")],
        "neoforge": [lib("YACL", "required"), lib("MaFgLib", "optional")]
    }
}

parser = argparse.ArgumentParser()
parser.add_argument("--token", "-t")
parser.add_argument("--version", "-v")
parser.add_argument("--loader", "-l")
args = parser.parse_args()
token = args.token
version = args.version
loader = args.loader

def create_version(version_code: str, version_type: str, changelog: str, game_versions: list[str], loader: str,
                   file: str, dependencies: list):
    version = f"{version_code}+{game_versions[-1]}-{loader}"
    data = json.dumps({
            "name": version,
            "version_number": version,
            "changelog": changelog,
            "game_versions": game_versions,
            "version_type": version_type,
            "loaders": [loader],
            "project_id": "BXNjmNox",
            "file_parts": [f"extended_interactions-{version}.jar"],
            "primary_file": f"extended_interactions-{version}.jar",
            "dependencies": dependencies,
            "featured": True
        })
    resp = requests.post("https://api.modrinth.com/v2/version", files={
        ("data", data.encode("utf-8")),
        (f"extended_interactions-{version}.jar", open(file, "rb"))
        #(f"extended_interactions-{version}.jar", open("mods.txt", "rb"))
    }, headers={
        "Authorization": token,
    })
    if not resp.ok:
        print("Failed to create version")
        print(data)
        print(resp.status_code)
        print(resp.text)
    else:
        print("Published", version)

def main():
    version = input("Version code (example 1.0.0): ")
    version_type = input("Version type (example release): ")
    with open("changelog.md", "r", encoding="utf8") as fr:
        changelog = fr.read()
    print(changelog)
    if input("Accept this changelog? [y/N] ").lower() != "y":
        return

    original = f"{loader}/versions/{mc}/build/libs/extended_interactions-{version}.jar"
    good_name = f"extended_interactions-{version}+{mc}-{loader}.jar"
    good = f"{loader}/versions/{mc}/build/libs/{good_name}"
    if not pathlib.Path(good).exists():
        pathlib.Path(original).rename(good)
    create_version(version, version_type, changelog, [mc], loader,
                   good, VERSIONS[mc][loader])

if __name__ == "__main__":
    main()