import string

def adjust_length(text_hex):
    remainder = len(text_hex) % 16
    if len(text_hex) < 16:
        text_hex += "0" * (16 - len(text_hex))
    elif remainder != 0:
        text_hex += "0" * remainder
    return text_hex 

def is_hex(text_hex):
    hexa = all(c in string.hexdigits for c in text_hex)
    if not hexa:
        return False
    return True

# Convert Hexadecimal stream to Bits stream
# Hex_stream ----> Bit_stream
def hex_to_bits(hex_stream):
    l_bits = []
    for i, _ in enumerate(hex_stream):
        bits = hex_to_bits_table(hex_stream[i])
        l_bits.append(bits)
    bit_stream = ''.join(l_bits)
    return bit_stream

# Hex to Bits Table 
def hex_to_bits_table(index):
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

# Convert a 6 bits to Number as follow 
# 1st bit and 6th bit(2 bits) : number[0]
# 2nd bits to 5th bit(4 bits) : number[1]
def bit_to_number(bit_stream):
    number = []
    part_1 = bit_stream[0:1]
    part_2 = bit_stream[5:6]
    number.append(int(part_1 + part_2, 2))
    part_3 = bit_stream[1:5]
    number.append(int(part_3, 2))
    return number

# Convert each 4 Bits stream to Hex stream 
def bits_to_hex(bit_stream): 
    if len(bit_stream) % 4 != 0:
        return 0
    l_hexa = []
    for i in range(0, len(bit_stream), 4):
        hexa = bits_to_hex_table(bit_stream[i:i+4])
        l_hexa.append(hexa)
    hex_stream = ''.join(l_hexa)
    return hex_stream

# Bits to Hex Table   
def bits_to_hex_table(index): 
    table = {
        "0000": "0",
        "0001": "1",
        "0010": "2",
        "0011": "3",
        "0100": "4",
        "0101": "5",
        "0110": "6",
        "0111": "7",
        "1000": "8",
        "1001": "9",
        "1010": "A",
        "1011": "B",
        "1100": "C",
        "1101": "D",
        "1110": "E",
        "1111": "F"}
    return table[index]

