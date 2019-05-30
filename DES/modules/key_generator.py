import string
# MODULE 1 
# This module aims to generate Round Keys 48x16 bits 
#-----Author RANAIVOSON 
#-----ROLL NO. 218CS2265

def generate(key_hex):
    '''
        Generate key function 
        INPUT : 
            #key_hex : Hexadecimal 64bits 
        OUTPUT : 
            #False : if key is not conform
            #list of keys (48bits for each slot{Total 16 slots}) : if key is conform
    '''
    if not is_conform(key_hex):
        return False
    
    # Convert Key_Hex to Key_Bits
    key_bits = hex_to_bits(key_hex) 
    
    # Parity Drop Function
    key_bits = parity_drop(key_bits)
    
    # Shift Left Function => #No. of shift for [0,1,2,8,15 Rounds] = 1 Left Shift / for [others] = 2 Left
    key_bits_shifted = {}
    for i in range(0, 16):
        if i in (0, 1, 8, 15):
            key_bits_shifted[i] = shift_left(key_bits, 1)
        else:
            key_bits_shifted[i] = shift_left(key_bits, 2)
        key_bits = key_bits_shifted[i]
        #print('Shifted Key {} : \t{}'.format(i, key_bits))

    # Compression D-BOX 
    keys = {}
    for i in range(0, 16):
        keys[i] = compression_dbox(key_bits_shifted[i])
    return keys

def is_conform(key_hex):
    '''
    Check conformity of #key_hex
    Conform is = 16 Hex string   
    '''
    is_hex = all(c in string.hexdigits for c in key_hex)
    if not is_hex or len(key_hex) != 16:
        return False
    return True


def shift_left(bit_stream, shift_step):
    '''
    Shift Left function 
    Shift each 28 bits of 56 bits in #bit_stream upon to #shift_no
    '''
    first_part = []
    # Append the 1st slice of the #bit_stream(from #shift_step to 28th bit)
    first_part.append(bit_stream[shift_step:28])
    # Append the 2nd slice of the #bit_stream(from 0 to #shift_step)
    first_part.append(bit_stream[0:shift_step])
    # Join the 2 slices to get the 1st PART
    first_part = ''.join(first_part)

    second_part = []
    # Append the 1st slice of the #bit_stream(from #shift_step to 28th bit)
    second_part.append(bit_stream[28+shift_step:57])
    # Append the 2nd slice of the #bit_stream(from 0 to #shift_step)
    second_part.append(bit_stream[28:28+shift_step])
    # Join the 2 slices to get the 1st PART
    second_part = ''.join(second_part)
    return ''.join(first_part+second_part)

def compression_dbox(bit_stream):
    '''
    Key Compression function (Ref. Table PC-2)
    56 bits --> 48 bits in bit_stream
    '''
    bit_stream_copy = {}
    for i in range(0, 48):
        index = dbox_table(i)
        bit_stream_copy[i] = bit_stream[index]
    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())

    return bit_stream

def dbox_table(index):
    '''
    PC-2: Compression Table functions contains 48 bits 
    '''
    dbox = [13, 16, 10, 23,  0,  4, \
             2, 27, 14,  5, 20,  9, \
            22, 18, 11,  3, 25,  7, \
            15,  6, 26, 19, 12,  1, \
            40, 51, 30, 36, 46, 54, \
            29, 39, 50, 44, 32, 47, \
            43, 48, 38, 55, 33, 52, \
            45, 41, 49, 35, 28, 31]
    return dbox[index]

def parity_drop(bit_stream):
    '''
    Parity Drop Function (Ref. Table PC-1) : 
    64 bits --> 56 bits in bit_stream
    '''
    bit_stream_copy = {}
    for i in range(0, 56):
        index = parity_table(i)
        bit_stream_copy[i] = bit_stream[index]

    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())

    return bit_stream

def parity_table(index):
    '''
    PC-1 : Parity table function contains 56 bits
    '''
    parity_drop_table = [56, 48, 40, 32, 24, 16,  8,  0, \
                         57, 49, 41, 33, 25, 17,  9,  1, \
                         58, 50, 42, 34, 26, 18, 10,  2, \
                         59, 51, 43, 35, 62, 54, 46, 38, \
                         30, 22, 14,  6, 61, 53, 45, 37, \
                         29, 21, 13,  5, 60, 52, 44, 36, \
                         28, 20, 12,  4, 27, 19, 11,  3]
    return parity_drop_table[index]

def hex_to_bits(hex_stream):
    '''
    Convert Input Hexadecimal Strings #hex_stream to Bits #bit_stream 
    '''
    l_bits = []
    for i, _ in enumerate(hex_stream):
        bits = hex_to_bin(hex_stream[i])
        l_bits.append(bits)
    bit_stream = ''.join(l_bits)
    return bit_stream

def hex_to_bin(index):
    '''
    Convert Input Hexadecimal Character to Bits 
    '''
    table = {
        "0": "0000",
        "1": "0001",
        "2": "0010",
        "3": "0011",
        "4": "0100",
        "5": "0101",
        "6": "0110",
        "7": "0111",
        "8": "1000",
        "9": "1001",
        "A": "1010",
        "B": "1011",
        "C": "1100",
        "D": "1101",
        "E": "1110",
        "F": "1111"}
    return table[index]
