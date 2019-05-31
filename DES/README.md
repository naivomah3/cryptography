# DES cipher algorithm
An implemetation from scratch of simple DES algorithm.
The objective is to get to know the internal functions and structure of DES. 
This takes as reference the book [CRYPTOGRAPHY AND NETWORK SECURITY][1] 
(Behrouz A. Forouzan & Debdeep Mukhopadhyay - 3rd Edition) <br/>
Data Encryption Standard(DES) is a Block Cipher algorithm based on Feistel schema. 
This implementation is a partial implementation of the DES algorithm and has only 
its educational purpose and proof of the correctness of the algorithm
described in the textbook mentioned above. As described in [Exemples 6.5 and 6.6][1], the output
shows the step-by-step trace of data processed by the internal DES functions.

```
Usage: 
$ python3 des_encryption.py <plain_text> <key>
$ python3 des_decryption.py <cipher_text> <key>

Example 6.5(Encryption):
plain_text: 123456ABCD132536 (64 bits)
cipher_text: C0B7A8D05F3A829C (64 bits)
key: AABB09182736CCDD (64 bits)
```



[1]: https://books.google.co.in/books?id=OYiwCgAAQBAJ&source=gbs_book_other_versions
