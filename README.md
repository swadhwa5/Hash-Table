## HW7 Discussion

1. Which hashmap approaches did you try, in what order, and why did you
   choose them? What specific tweaks to HashMap improved or
   made things worse?  Include failed or abandoned attempts if any, and
   why. Summarize all the different ways you developed, evaluated and
   improved your hashmaps.

I started with Chaining and then went on to open addressing. The first implementation of HashMap I did was the 
HashMapLinkedListChaining which stores all the values mapped to keys with the same index in a Linked List. 
This results in the overall structure being an array of Linked Lists, where the Linked List is a list of 
Entry<K, V>. This was followed by attempts at all the Open addressing techniques, including linear probing, 
quadratic probing, and double hashing. It is important to note that the runtime for Linked List chaining depends 
on its load factor solel, as find(): O(A) ; delete(): O(A) ; insert() : O(1) ;where A is the load factor 
num_elements/capacity. Whereas in Open addressing, most operations are O(1) or amortised O(1), Therefore, 
open addressing should perform better than Linked list Chaining. Within open addressing, I started with 
Linear Probing, since once I’m done with Linear Probing, it is to replicate the mechanism for quadratic 
probing and Double Hashing, as the only difference between them is the jump to the next index while searching 
or inserting. 

Next, I tried to play with each of  these 4 factors in all implementations while  keeping the rest constant:
 1. Load Factor threshold for rehashing and grow()
 2. Capacity
 3. New Capacity while rehashing 

Baseline Implementation with load factor threshold >0.5, hash function being key.hascode() % capacity, 
capacity being 13 and newCapacity being the smallest prime more than or equal to 2 * capacity 

For LinkedList chaining, we don’t essentially require the grow() functionality. But when we remove the grow(), 
it performs much worse as the load factor ‘A’ keeps increasing and since find() and delete() for linkedList 
chaining are O(A), it performs badly. On the rest, a slight increase in performance is seen when the initial 
capacity is changed from 13 to 29, since it decreases the number of times grow() is called and also decreases 
the load factor for the same number of elements, that helps in avoiding clustering. Increasing the LoadFactor 
threshold to > 0.75 doesn;t make any significant difference and hence is left to > 0.5. Changing the new capacity 
to 2 * capacity doesn't make a significant difference in performance but we know that it is good to have a prime 
number capacity at all times to new capacity remains smallest prime more than or equal to 2 * capacity.

Also, none of these differences are observed for urls.txt as the number of elements is less than half of the 
capacity initially, so grow() is never called and load factor threshold is never used. So evaluations are done 
on text files like apache.txt which have a larger number of elements.


2. Include all the benchmarking data, results and analysis that contributed to your final
decision on which implementation to use for the search engine.


Urls.txt:
LinkedListChaining: 24 ms using 683 kb memory
LinearProbing: 22 ms using 665 kb memory
QuadraticProbing: 23 ms using 665 kb memory
DoubleHashing: 22 ms using 665 kb memory
AvlMap: 22 ms using 675 kb memory
TreapMap: 23 ms using 665 kb memory
JDKHashMap: 20 ms using 683 kb memory
Performance: 
Time: JDKHashMap > DoubleHashing = Linear Probing = AvlMap > Quadratic probing = Treap > LinkedListChaining
Space: DoubleHashing = Quadratic Probing = Linear Probing = TreapMap > AvlMap > LinkedListChaining = JDKHashMap

Joanne.txt:
LinkedListChaining: 26 ms using 683 kb memory
LinearProbing: 23 ms using 665 kb memory
QuadraticProbing: 25 ms using 665 kb memory
DoubleHashing: 24 ms using 665 kb memory
AvlMap: 25 ms using 675 kb memory
TreapMap: 26 ms using 665 kb memory
JDKHashMap: 24 ms using 683 kb memory
Performance: 
Time: Linear probing > Double Hashing = JDK HashMap > Quadratic Probing = AvlMap > LinkedList Chaining > Treap
Space: DoubleHashing = Quadratic Probing = Linear Probing = TreapMap > AvlMap > LinkedListChaining = JDKHashMap


JHU.txt:
LinkedListChaining: 39 ms using 1348 kb memory
LinearProbing: 31 ms using 1331 kb memory
QuadraticProbing: 31 ms using 1331 kb memory
DoubleHashing: 29 ms using 1331 kb memory
AvlMap: 34 ms using 1340 kb memory
TreapMap: 31 ms using 1331 kb memory
JDKHashMap: 33 ms using 1348 kb memory
Performance: 
Time: DoubleHashing > Quadratic Probing = Linear Probing = Treap Map > JDKHashMap > AvlMap > LinkedList Chaining
Space: DoubleHashing = Quadratic Probing = Linear Probing = TreapMap > AvlMap > LinkedListChaining = JDKHashMap


Newegg.txt:
LinkedListChaining: 407 ms using 73673 kb memory
LinearProbing: 390 ms using 65224 kb memory
QuadraticProbing: 373 ms using 64347 kb memory
DoubleHashing: 349 ms using 65254 kb memory
AvlMap: 440 ms using 63964 kb memory
TreapMap: 429 ms using 63850 kb memory
JDKHashMap: 396 ms using 63135 kb memory
Performance: 
Time: Double Hashing > Quadratic Probing > Linear Probing >JDKHashMap >  LinkedList Chaining > Treap > AvlMap
Space: JDKHashMap > Treap > AvlMap > Quadratic Probing > Linear Probing > Double Hashing 


Apache.txt:
LinkedListChaining: 670 ms using 62399 kb memory
LinearProbing: 596 ms using 49380 kb memory
QuadraticProbing: 634 ms using 49492 kb memory
DoubleHashing: 609 ms using 49394 kb memory
AvlMap: 722 ms using 46539 kb memory
TreapMap: 789 ms using 46551 kb memory
JDKHashMap:574 ms using 46904 kb memory
Performance: 
Time: JDKHashMap > Linear Probing > Double Hashing > Quadratic Probing > LinkedList Chaining > AvlMap > Treap
Space: AvlMap > Treap > JDKHashMap > Linear Probing > Double Hashing > Quadratic Probing > LinkedList Chaining


Random164.txt:
LinkedListChaining: 1474 ms using 206438 kb memory
LinearProbing: 1232 ms using 318478 kb memory 
QuadraticProbing: 1176 ms using 318468 kb memory
DoubleHashing: 1139 ms using 318364 kb memory
AvlMap: 2049 ms using 296458 kb memory
TreapMap: 2295 ms using 296557 kb memory.
JDKHashMap: 1089 ms using 312962 kb memory
Performance: 
Time: JDKHashMap > Double Hashing > Quadratic Probing > Linear Probing > Linkedlist Chaining > Avl Map > Treap
Space: LinkedList Chaining > AvlMap > TreapMap > JDKHashMap > Double Hashing > Quadratic Hashing > Linear Probing


3. Provide an analysis of your benchmark data and conclusions. Why did
   you choose your final HashMap implementation as the best one? What 
   results were surprising and which were expected?
 
Time: Analysing the data, when compared to JDKHashMap, it can be concluded that Double Hashing performs the 
best in most scenarios, followed by Quadratic Probing, Linear Probing and then Linked List Chaining. That is 
because the Open Addressing has been runtime than chaining in general and within open addressing, the hashing 
in double hashing is set in a way that prevents clustering the best. 

Space: In terms of space, using the benchmarking data, it can be concluded that open addressing is  more space 
efficient than Linkedlist chaining and that  is because LinkedList Chaining involves Entries with a ‘next’ 
parameter in addition to ‘key’ and ‘value’, that takes up more space than an array element.Now within open 
addressing, double hashing is the most space efficient as shown by the benchmarking data and hence Double 
hashing should be used, followed by quadratic, and Linear.

According to both Time and space analysis, Double Hashing proves to be the best and hence I decided to use 
Double Hashing as my final technique.

