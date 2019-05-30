import sys
from modules import key_generator as keygen
from modules import helper as h
from modules import cipher as enc

def main():
    #Check the number of Arguments to be 3 
    if len(sys.argv) != 3:
        print("Usage : python3 des_decryption <hexa_cipher_text> <hexa_key>")
        exit()
    #Check the size of the Plaintext and Key
    if not len(sys.argv[1]) == 16:
        print("{} should be encoded in HEX of size 64 bits ".format(sys.argv[1]))
        exit()
    if not len(sys.argv[2]) == 16:
        print("{} should be encoded in HEX of size 64 bits ".format(sys.argv[2]))
        exit()

    #cipher_text = "C0B7A8D05F3A829C"
    cipher_text = sys.argv[1].upper()
    
    #Check if cipher_text string is encoded in HEX 
    if not h.is_hex(cipher_text):
        print("Ciphertext : \'{}\', should be encoded in HEX, double check".format(cipher_text))
        exit()
    middle_text = h.hex_to_bits(cipher_text)    #became 
    print("\nCipher Text : \t\t\t{} | {}".format(cipher_text, middle_text))

    #--------------KEY GENERATOR----------------#
    #key_hex = "AABB09182736CCDD"
    key_hex = sys.argv[2].upper()
    #Check if KEY srting is coded in HEX 
    if not h.is_hex(key_hex):
        print("Key : \'{}\', should be encoded in HEX, double check".format(key_hex))
    print('Key :\t\t\t\t{} | {}'.format(key_hex,h.hex_to_bits(key_hex)))

    #Generate a Dictionary key in Keys 
    #48 bits for each slot
    keys = keygen.generate(key_hex)
    #____________END KEY GENERATOR______________#


    #------------INITIAL PERMUTATIONN-------------#
    #Initial Permuation Function 
    #Middle text 64 bits 
    middle_text = enc.initial_perm(middle_text)
    #print("Middle Text : \t\t\t\t{}".format(middle_text))
    #_________END INITAL PERMUATIOTION____________#


    #---------------ENCRYPTION-------------------#
    #Split Right and Left Function 
    #Middle text 64 bits --> Right(32 bits) / Left(32 bits)
    middle_text_left_part = middle_text[0:32]
    middle_text_right_part = middle_text[32:64]
    #print("Initial Permutation Left Part : \t{}".format(middle_text_left_part))
    #print("Initial Permutation Right Part : \t{}".format(middle_text_right_part))
    print("After Initial Permutation : \t{} | {}".format(h.bits_to_hex(middle_text[0:64]), middle_text[0:64]))

    #Store data in a Dictionary : 'middle_text' 
    # Each slot of 'middle_text' contains dictionary 'partition'
    # Each slot of 'middle_text' ==> 'partition[0]': 32 bits 'partition[1]': 32 bits 
    # Total: 64 bits 
    middle_text = {}
    partition = {}
    
    #Initialisation of 'middle_text' 
    # middle_text[0] == the first partition  
    partition[0] = middle_text_left_part
    partition[1] = middle_text_right_part
    middle_text[16] = partition

    #Printing part
    print("\nRound \t\t|\t Left \t\t|\t Right \t\t|\t Round Key \t|")
    print("-----------------------------------------------------------------------------------------")
    for i in range(15,-1,-1):
        #Temporary store the current LEFT and RIGHT part into "partition"
        partition = {}

        #Update the current LEFT partition[0]
        #Current LEFT partition[0] == previous RIGHT partition  
        partition[0] = middle_text[i+1][1]

        #Update the current RIGHT partition[1]
        #Expansion Function 
        #Step 1 : Expand the previous RIGHT partition and make it as the current RIGHT partition
        #Right(32 bits) ---> 48 bits
        partition[1] = enc.expansion(middle_text[i+1][1])
        #print("Expansion RIGHT partition[1] : \t\t{}".format(partition[1]))

        #XOR-ing Function 
        #Step 2 : Current RIGHT partition XOR-ing with  KEY[i+1] 
        #Right(48 bits) xor Key(48 bits) ---> 48 bits
        partition[1] = enc.xoring(partition[1], keys[i], 48)
        #print("X
        # OR-ing RIGHT partition[1] : \t\t{}".format(partition[1]))

        #Substitution Function 
        #Step 3 : substitute each block of 6 bits of the Current RIGHT partition 48 bits  
        #to get each block of 4 bits and update the Current RIGHT partition to 32 bits
        #Right(48 bits) ---> 32 bits
        partition[1] = enc.substitution_table(partition[1])
        #print("Substitution partition[1] : \t\t{}".format(partition[1]))
        
        #Permutation Function 
        #Step 4 : last permutation in the 'f' function
        #Right(32 bits) --> 32 bits 
        partition[1] = enc.permutation(partition[1])
        #print("Permutation partition[1] : \t\t{}".format(partition[1]))

        #Last XOR-ing Function 
        #Step 5 : Xoring 
        #Right(32 bits) XOR Left(32 bits)
        partition[1] = enc.xoring(middle_text[i+1][0], partition[1], 32)
        #print("Last XOR-ing partition[1] : \t\t{}".format(partition[1]))

        #Update the Middle Text 
        #Add the last "'partition'" in the Middle Text Dictionary 
        middle_text[i] = partition
        #print("middle_text : \t\t{}".format(middle_text))

        #Print LEFT(32 bits) and RIGHT(32 bits)
        print("Round {} \t|\t {} \t|\t {} \t|\t {} \t|\n".format(i+1, h.bits_to_hex(middle_text[i][0]), h.bits_to_hex(middle_text[i][1]), h.bits_to_hex(keys[i])))
    #_____________END ENCRYPTION_________________#


    #------------FINAL PERMUTATIONN-------------#
    #Combine L16 LEFT(32 bits) and R16 RIGHT(32 bits) in reverse order before the Final Permutation 
    #32 bits x 2 ----> 64 bits 
    #Middle Text 16 : R16(32 bits) + L16(32 bits) (Reverse order)
    middle_text = ''.join(middle_text[0][i] for i in sorted(middle_text[0].keys(), reverse=True))
    print("After reverse combination: \t{} | {}".format(h.bits_to_hex(middle_text), middle_text))


    #Final Permuation Function 
    #Middle text 64 bits ---> Encrypted Text 64 bits 
    middle_text = enc.final_perm(middle_text)
    #print("Final Permutation - Cipher Plain : \t{}".format(middle_text))

    #Convert the Cipher Text to Hexadecimal 
    middle_text = h.bits_to_hex(middle_text)
    print("After Final Permuation: \t{} | {}".format(middle_text, h.hex_to_bits(middle_text)))
    #_________END FINAL PERMUATIOTION____________#

    #Print the Cipher Text
    print("\nPlain Text: \t\t\t{}\n".format(middle_text))    


if __name__ == "__main__":
    main()
