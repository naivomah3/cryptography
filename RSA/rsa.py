import random
import base64
import argparse
from modules import helper as h

def main():
    # Args parsing 
    plaintext = ''
    ciphertext = ''
    params = []
    parser = argparse.ArgumentParser(description="RSA algorithm")
    parser.add_argument('-e', '--encrypt', action='store', dest='plaintext', help='The message to encrypt', required=False)
    parser.add_argument('-d', '--decrypt', action='store', dest='ciphertext', help='The message to decrypt', required=False)
    parser.add_argument('-u', '--pubkey', type=int, action='store', dest='pub_key', help='Public-key', required=False)
    parser.add_argument('-r', '--privkey', type=int, action='store', dest='priv_key', help='Private-key', required=False)
    parser.add_argument('-m', '--modulo', type=int, action='store', dest='modulo', help='Modulo', required=False)
    parser.add_argument('-g', '--generate', action='store_true', help='Generate key pair (public-private)')
    args = parser.parse_args()

    if args.generate:
        params = h.generate_params()
        print('Public Key: {}\nPrivate Key: {}\nMod: {}'.format(params[0], params[1], params[2]))

    if args.plaintext: 
        if args.pub_key and args.modulo:
            ciphertext = rsa_encrypt(args.plaintext, args.pub_key, args.modulo)
            print('Ciphertext: {}'.format(ciphertext))
        else:
            raise ValueError('Missing params')

    if args.ciphertext:
        if args.priv_key and args.modulo:
            plaintext = rsa_decrypt(args.ciphertext, args.priv_key, args.modulo)
            print('Plaintext: {}'.format(plaintext))
        else:
            raise ValueError('Missing params')


def rsa_encrypt(message, e, n):
    encrypted_blocks = []
    for block in h.text_to_ascii(message):
        encrypted_blocks.append(str(pow(block, e, n)))
    cipher_text = "___".join(encrypted_blocks)
    cipher_text = cipher_text.encode('utf8')
    cipher_text = base64.b64encode(cipher_text)
    return cipher_text

def rsa_decrypt(message, d, n):
    message = base64.b64decode(message)
    message = message.decode('utf8')
    decrypted_blocks = []
    message = message.split("___")
    for block in message:
        decrypted_blocks.append(str(pow(int(block), d, n)))
    decrypted_text = "___".join(decrypted_blocks)
    decrypted_text = h.ascii_to_text(decrypted_text)
    decrypted_text = "".join(decrypted_text)
    return decrypted_text

if __name__ == "__main__":
    main()

