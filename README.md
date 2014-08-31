
GiftSelection
======
###Problem Description
---
When giving gifts, consumers usually keep in mind two variables - cost and quantity. 
In order to facilitate better gift-giving on the Zappos website, the Software Engineering team would like to test a simple application that allows a user to submit two inputs: N (desired # of products) and X (desired dollar amount). The application should take both inputs and leverage the Zappos API to create a list of Zappos products whose combined values match as closely as possible to X dollars. For example, if a user entered 3 (# of products) and $150, the application would print combinations of 3 product items whose total value is closest to $150.

###Solution 
---
There are two paramters that are involved here
-  Number of items to be purchased
-  Total cost of the items to be purchased

The following recursive solution will create a list of price combination that statisfy the above two condition.

```
canProceed(int current, ArrayList<Integer> selected) {
		currentSumStatus = inRange(selected);
		IF (selected.size == N) {
			// We got the combination with the max given limit.
			IF (currentSumStatus == 0) {
				PRINT(selected);
				RETURN TRUE; 
			// We need to keep checking for other combinations of size N
			} ELSE IF (currentSumStatus == -1) {
				RETURN TRUE; // Smaller, other numbers can make it bigger.
			} ELSE IF (currentSumStatus == 1) {
				RETURN FALSE; // Already big, no point scanning further.
			}
		} ELSE IF (selected.size < N) {
			IF (currentSumStatus == 0) {
				// We need to keep checking since there is still a chance for
				// other combinations
			} ELSE IF (currentSumStatus == -1) {
				// Smaller, other numbers can make it bigger.
			} ELSE IF (currentSumStatus == 1) {
				RETURN FALSE; // Already big, no point scanning further.
			}
		} ELSE IF (selected.size() > N) {
			RETURN FALSE;
		}

		FOR (int i = current; i < COST.length; i++) {
			selected.add(COST[i]);
			IF (!canProceed(i + 1, selected)) {
				BREAK;
			} ELSE {
				selected.remove(selected.size - 1);
			}
		}
		RETURN TRUE;
	}
```

For example if there are two arrays called `PRICE` and `COST` that hold the item's price and its name
```
PRICE = [a,b,c,d,e,f,g,h]
COST = [1,2,2,5,5,6,7,8]
```

If the input parameters are N = 3 and C = 10, and since we want an approximation the above algorithm will also print price combinations that are + or -  the total cost. If + or - 2 is allowed the recursive function will print the following
```
[1,2,5] [1,5,5] [1,5,6]
[2,2,5] [2,2,8] [5,6,1]
[5,1,5] [5,2,2] [5,2,1]  
[6,5,1] [6,1,2]
```
That is all price combinations whose total value lies in the range 8 to 12 are printed

Next all the price are stored in a hashmap with price as the key and the values as the items's name

Catesian product is found for the above lists and using the hashamp to generate all possible item price and item name combinations
