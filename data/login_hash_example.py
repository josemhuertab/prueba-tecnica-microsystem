import hashlib


def generate_login_hash(username: str, pwd: str) -> str:
    """
    Genera un hash SHA-256 combinando nombre de usuario y contraseña.
    """
    combined = f"{username}:{pwd}"
    hash_value = hashlib.sha256(combined.encode('utf-8')).hexdigest()
    return hash_value


if __name__ == "__main__":
    user = "JuanPerezDelCampo001"
    password = "password123"
    login_hash = generate_login_hash(user, password)
    print(f"Hash de login: {login_hash == 'a973311dbb6f28cb9b7a87bf92cb33316e739b153eab4c6d68338ec74a8b1600'}")
