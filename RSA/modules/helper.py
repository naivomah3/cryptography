import random
import base64

def is_prime(num):
    # Return True if num is a prime

    if num < 2:
        return False # 0, 1, and negative numbers are not prime

    # Low primes table 
    # used to check directly if num is in
    # call rabin_miller() otherwise
    low_primes_table = [2,  3,  5,  7, 11, 13, 17, 19, 23, 29, \
                        31, 37, 41, 43, 47, 53, 59, 61, 67, 71, \
                        73, 79, 83, 89, 97,101,103,107,109, 113, \
                        127, 131, 137, 139, 149, 151, 157, 163, \
                        167, 173, 179, 181, 191, 193, 197, 199, \
                        211, 223, 227, 229, 233, 239, 241, 251, \
                        257, 263, 269, 271, 277, 281, 283, 293, \
                        307, 311, 313, 317, 331, 337, 347, 349, \
                        353, 359, 367, 373, 379, 383, 389, 397, \
                        401, 409, 419, 421, 431, 433, 439, 443, \
                        449, 457, 461, 463, 467, 479, 487, 491, \
                        499, 503, 509, 521, 523, 541, 547, 557, \
                        563, 569, 571, 577, 587, 593, 599, 601, \
                        607, 613, 617, 619, 631, 641, 643, 647, \
                        653, 659, 661, 673, 677, 683, 691, 701, \
                        709, 719, 727, 733, 739, 743, 751, 757, \
                        761, 769, 773, 787, 797, 809, 811, 821, \
                        823, 827, 829, 839, 853, 857, 859, 863, \
                        877, 881, 883, 887, 907, 911, 919, 929, \
                        937, 941, 947, 953, 967, 971, 977, 983, \
                        991, 997]

    if num in low_primes_table:
        return True

    # See if any of the low prime numbers can divide num
    for prime in low_primes_table:
        if num % prime == 0:
            return False

    # If all else fails, call rabin_miller() to determine if num is a prime.
    return rabin_miller(num)

def rabin_miller(num):
    '''
    Rabin-Miller test function
    10 times trials for effective result
    '''
    s = num - 1
    t = 0
    while s % 2 == 0:
        s = s // 2
        t += 1

    for t in range(10): 
        a = random.randrange(2, num - 1)
        v = pow(a, s, num)
        if v != 1: 
            i = 0
            while v != (num - 1):
                if i == t - 1:
                    return False
                else:
                    i = i + 1
                    v = (v ** 2) % num
    return True

def generate_prime(key_size=6):
    '''
    Prime generator function
    '''
    while True:
        num = random.randrange(2**(key_size-1), 2**(key_size))
        if is_prime(num):
            return num

def gcd(a, b):
    '''
    GCD calculator using Euclidean algorithm 
    Returns GCD of a,b
    '''
    while a != 0:
        a, b = b % a, a
    return b

def mult_inverse_mod(a, m):
    '''
    Multiplicative Inverse-Modulo function using Extended Euclidean Algorithm. 
    Returns b IF exists b such that (a * b) % m == 1
            none otherwise 
    '''
    if gcd(a, m) != 1:
        return None

    u1, u2, u3 = 1, 0, a
    v1, v2, v3 = 0, 1, m
    while v3 != 0:
        q = u3 // v3 
        v1, v2, v3, u1, u2, u3 = (u1 - q * v1), (u2 - q * v2), (u3 - q * v3), v1, v2, v3
    return u1 % m

def generate_params():
    '''
    Params generator: 
    p and q: the 2 large primes 
    n: product of p and q
    e: randomly taken such that 1 < e < φ(n) and φ(n) = (p-1)(q-1)
    d: 
    '''
    key_size = 6 
    p = 0
    q = 0
    n = 0 
    e = 0
    d = 0
    is_e = True
    p = generate_prime(key_size)
    q = generate_prime(key_size)
    n = p * q
    while True:
        e = random.randrange(2**(key_size-1), 2**(key_size))
        if gcd(e, (p-1)*(q-1)) == 1:
            break
    #print("e = {}".format(e))

    d = mult_inverse_mod(e, (p-1)*(q-1))
    #print("d = {}".format(d))
    return (e, d, n)

def text_to_ascii(message):
    message_bytes = message.encode('ascii')
    #print([i for i in str(message_bytes)])
    block_ints = []
    for i in range(len(message_bytes)):
        block_ints.append(message_bytes[i])
    return block_ints

def ascii_to_text(block_messages):
    blocks = []
    block_messages = block_messages.split("___")
    for block in block_messages:
        blocks.append(str(chr(int(block))))
    return blocks