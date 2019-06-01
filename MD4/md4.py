import sys
from modules import helper as h

def main():
    if len(sys.argv) != 2:
        print("Usage: \npython3 md4.py \"<your_message>\"")
        print("# Note: \"<your_message>\" should be within double quotes\n")
        exit()

    message = sys.argv[1]
    print("\nOriginal message ({1} bits) :\n  \"{0}\" ".format(message, len(message)*8))
    
    md4_hash = h.md4(message, True)
    print("\nHash (128 bits) : {}\n".format(md4_hash.upper()))

if __name__ == "__main__":
    main()
    