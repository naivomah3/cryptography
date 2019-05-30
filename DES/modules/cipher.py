import modules.helper as h
# MODULE 2
# This module contains functions used in DES Encryption 
#-----Author RANAIVOSON 
#-----ROLL NO. 218CS2265

# Initial Permuation Function / Permute bit (Ref. IP table)
# Input : 64 bits 
# Output : 64bits
def initial_perm(bit_stream):
    bit_stream_copy = {}
    for i in range(0, 64):
        index = initial_perm_table(i)
        bit_stream_copy[i] = bit_stream[index]
    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())

    return bit_stream  
#IP table : for Initial Permutation table 
def initial_perm_table(index):
    fp_table = [57, 49, 41, 33, 25, 17,  9, 1, \
                59, 51, 43, 35, 27, 19, 11, 3, \
                61, 53, 45, 37, 29, 21, 13, 5, \
                63, 55, 47, 39, 31, 23, 15, 7, \
                56, 48, 40, 32, 24, 16,  8, 0, \
                58, 50, 42, 34, 26, 18, 10, 2, \
                60, 52, 44, 36, 28, 20, 12, 4, \
                62, 54, 46, 38, 30, 22, 14, 6]

    return fp_table[index]

# FP Function = Final Permuation Function / Permute bit (Ref. FP table)
# Input : 64 bits 
# Output : 64bits
def final_perm(bit_stream):
    bit_stream_copy = {}
    for i in range(0, 64):
        index = final_perm_table(i)
        bit_stream_copy[i] = bit_stream[index]
    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())
    return bit_stream 

# IP table : for Initial Permutation table 
def final_perm_table(index):
    fp_table = [39, 7, 47, 15, 55, 23, 63, 31, \
                38, 6, 46, 14, 54, 22, 62, 30, \
                37, 5, 45, 13, 53, 21, 61, 29, \
                36, 4, 44, 12, 52, 20, 60, 28, \
                35, 3, 43, 11, 51, 19, 59, 27, \
                34, 2, 42, 10, 50, 18, 58, 26, \
                33, 1, 41,  9, 49, 17, 57, 25, \
                32, 0, 40,  8, 48, 16, 56, 24]
    return fp_table[index]


# Expansion Function = Expand 32 bits stream to 48 bits stream (Ref. Expansion Table)
# Input : 32 bits 
# Output : 48 bits 
def expansion(bit_stream):
    bit_stream_copy = {}
    for i in range(0, 48):
        index = expansion_table(i)
        bit_stream_copy[i] = bit_stream[index]
    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())

    return bit_stream

# Expansion Table 
def expansion_table(index):
    table = [31,  0,  1,  2,  3,  4,  3,  4, \
              5,  6,  7,  8,  7,  8,  9, 10, \
             11, 12, 11, 12, 13, 14, 15, 16, \
             15, 16, 17, 18, 19, 20, 19, 20, \
             21, 22, 23, 24, 23, 24, 25, 26, \
             27, 28, 27, 28, 29, 30, 31, 0]
    return table[index]

# XOR-ing Function 
# input : 48 bits 
# output : 48 bits 
def xoring(bit_stream_x, bit_stream_y, bit_stream_size):
    if bit_stream_size == 32:
        return '{0:032b}'.format(int(bit_stream_x, 2) ^ int(bit_stream_y, 2))
    return '{0:048b}'.format(int(bit_stream_x, 2) ^ int(bit_stream_y, 2))
        

# Substitution Function = Compression D-BOX 
# Compresses 48 bits(8 block of 6 bits [6x8=48]) to get 32 bits 
# input : 48 bits 
# output : 32 bits 
def substitution_table(bit_stream):
    list_bits = []
    for i in range(0,48,6):
        # Extract 6 bits
        bits = bit_stream[i:i+6]
        index = h.bit_to_number(bits)

        # Substitute each 6 bits to 4 bits 
        # upon the value getting from substitution_table_X  
        if i == 0:
            value = substitution_table_1(index[0],index[1])
        elif i == 6:
            value = substitution_table_2(index[0],index[1])
        elif i == 12:
            value = substitution_table_3(index[0],index[1])
        elif i == 18:
            value = substitution_table_4(index[0],index[1])
        elif i == 24:
            value = substitution_table_5(index[0],index[1])
        elif i == 30:
            value = substitution_table_6(index[0],index[1])
        elif i == 36:
            value = substitution_table_7(index[0],index[1])
        elif i == 42:
            value = substitution_table_8(index[0],index[1])
        bits = "{0:04b}".format(value)
        list_bits.append(bits)

    bit_stream = ''.join(list_bits)
    return bit_stream

# Sub table 1 
def substitution_table_1(row,col):
    table = [[14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7],
             [0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8],
             [4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0],
             [15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13]]
    return table[row][col]

# Sub table 2 
def substitution_table_2(row,col):
    table = [[15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10],
             [3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5],
             [0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15],
             [13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9]]
    return table[row][col]

# Sub table 3
def substitution_table_3(row,col):
    table = [[10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8],
             [13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1],
             [13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7],
             [1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12]]
    return table[row][col]

# Sub table 4
def substitution_table_4(row,col):
    table = [[7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15],
             [13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9],
             [10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4],
             [3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14]]
    return table[row][col]

# Sub table 5
def substitution_table_5(row,col):
    table = [[2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9],
             [14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6],
             [4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14],
             [11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3]]
    return table[row][col]

# Sub table 6
def substitution_table_6(row,col):
    table = [[12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11],
             [10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8],
             [9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6],
             [4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13]]
    return table[row][col]

# Sub table 7
def substitution_table_7(row,col):
    table = [[4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1],
             [13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6],
             [1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2],
             [6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12]]
    return table[row][col]

# Sub table 8
def substitution_table_8(row,col):
    table = [[13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7],
             [1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2],
             [7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8],
             [2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11]]
    return table[row][col]

# Permutation Function  
def permutation(bit_stream):
    bit_stream_copy = {}
    for i in range(0, 32):
        index = permutation_table(i)
        bit_stream_copy[i] = bit_stream[index]
    bit_stream = ''.join(value for keys, value in bit_stream_copy.items())

    return bit_stream

# Permuation Table 
def permutation_table(index):
    table = [15,  6, 19, 20, 28, 11, 27, 16, \
              0, 14, 22, 25,  4, 17, 30,  9, \
              1,  7, 23, 13, 31, 26,  2,  8, \
             18, 12, 29,  5, 21, 10,  3, 24]
    return table[index]
