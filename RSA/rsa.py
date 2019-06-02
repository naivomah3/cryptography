import random
import base64
from modules import helper as h

def rsa_encrypt(message, e, n):
    encrypted_blocks = []
    for block in h.text_to_ascii(message):
        encrypted_blocks.append(str(pow(block, e, n)))
    #print(encrypted_blocks)
    cipher_text = "___".join(encrypted_blocks)
    cipher_text = cipher_text.encode('utf8')
    cipher_text = base64.b64encode(cipher_text)
    return cipher_text

def rsa_decryption(message, d, n):
    message = base64.b64decode(message)
    message = message.decode('utf8')
    decrypted_blocks = []
    message = message.split("___")
    for block in message:
        decrypted_blocks.append(str(pow(int(block), d, n)))
    decrypted_text = "___".join(decrypted_blocks)
    decrypted_text = h.ascii_to_text(decrypted_text)
    decrypted_text = "".join(decrypted_text)
    #format(message).encode('utf8')
    return decrypted_text


if __name__ == "__main__":
    (e, d, n) = h.generate_params()
    plain_text = 'a long text'
    encrypted_block = rsa_encrypt(plain_text, e, n)
    decrypted_block = rsa_decryption(encrypted_block, d, n)
    message = decrypted_block