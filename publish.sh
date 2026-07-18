token=$((kwallet-query -r modrinth denisjava -f tokens))
./.venv/bin/python publish.py --token $token --version 1.21.1 --loader fabric
./.venv/bin/python publish.py --token $token --version 1.21.1 --loader neoforge
./.venv/bin/python publish.py --token $token --version 1.21.11 --loader fabric
./.venv/bin/python publish.py --token $token --version 1.21.11 --loader neoforge