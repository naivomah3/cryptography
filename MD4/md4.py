import argparse
from codecs import decode
from os import sys
from modules import helper as h


def main():
    # Args parsing 
    parser = argparse.ArgumentParser(description="Hashing algorithm")
    parser.add_argument('-m', '--message', action='store', dest='message', help='The message to hash', required=True)
    args = parser.parse_args()

    message = args.message
    print("\nOriginal message ({1} bits) :\n  \"{0}\" ".format(message, len(message)*8))

    md4_hash = h.md4(message, False)
    print("\nHash value (128 bits) : {}\n".format(md4_hash.decode('utf-8')))

if __name__ == "__main__":
    main()
