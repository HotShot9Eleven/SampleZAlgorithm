import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ZAlgorithm {
	final static int NoOfItemsToBereturned = 20;
	final static String ITEM[] = new String[NoOfItemsToBereturned];
	final static int COST[] = new int[NoOfItemsToBereturned];
	// items cost
	// each
	// item is the
	// index of the
	// array.
	static int N; // Only this many items allowed.
	static int C; // Total Cost allowed.
	final static int whatIsApproximate = 5; // This checks for the range C + or
											// - whatIsApproximate

	static ArrayList<String> PriceList = new ArrayList<String>();
	static ArrayList<String> ProductName = new ArrayList<String>();

	final static ArrayList<ArrayList<Integer>> mList = new ArrayList<ArrayList<Integer>>();

	public static void main(String args[]) {
		ArrayList<ArrayList<Integer>> uList = new ArrayList<ArrayList<Integer>>();
		TreeSet<Integer> PriceSet = new TreeSet<Integer>();
		HashMap<Integer, Set<String>> ItemMap = new HashMap<Integer, Set<String>>();

		// Take input from the user
		Scanner input = new Scanner(System.in);

		System.out.println("Enter number of Gifts to be purchased");
		N = input.nextInt();

		System.out
				.println("Enter the total price of the gifts which you would like to purchase");
		C = input.nextInt();

		System.out
				.println("Enter a keyword or a few words that best describes the type of gift you are looking for");
		String KeyWordToSearch = input.next();

		String ZapposSearch = "http://api.zappos.com/Search/term/"
				+ KeyWordToSearch + "?sort={%22price%22:%22asc%22}&limit="
				+ NoOfItemsToBereturned + "&key=";

		String api_key = "a73121520492f88dc3d33daf2103d7574f1a3166";

		// Make the Zappos API call to fetch the data that is needed
		getJSONResponse(ZapposSearch, api_key);

		// Format the price field of the json response when the API is called
		currencyFormatter(PriceList);

		initializeProductArray(ProductName);

		System.out.println(COST);

		System.out.println(ITEM);

		canProceed(0, new ArrayList<Integer>());

		// Set all the flags to zero indicating all the price combinations are valid 
		int myflag[] = new int[mList.size()];
		for (int i = 0; i < myflag.length; i++) {
			myflag[i] = 0;
		}

		System.out.println("");

		// Select Unique Price Combinations from the list of valid Array List
		// For Example [8,5,4] and [5,4,8] will be compared and only [5,4,8]
		// will be stored in uList
		// Compare each list's with every other list.  If the two list are having the same
		// elements assign -1 to one list and 1 to the other list. Lists that are having 
		// 0 are yet to be compared.
		for (int i = 0; i < mList.size(); i++) {

			if (myflag[i] == 0) {
				ArrayList<Integer> currentList = new ArrayList<Integer>(
						mList.get(i));
				for (int j = i + 1; j < mList.size(); j++) {

					ArrayList<Integer> nextList = new ArrayList<Integer>(
							mList.get(j));

					Collections.sort(currentList);
					Collections.sort(nextList);

					if (currentList.equals(nextList)) {
						myflag[i] = 1;
						myflag[j] = -1;
					} else {
						myflag[i] = 1;
					}
				}

			}

		}

		// Add lists whose flag values are either 1 or 0
		for (int i = 0; i < myflag.length; i++) {
			if ((myflag[i] == 0) || (myflag[i] == 1)) {
				uList.add(mList.get(i));
			}
		}

		// Collect all unique price and add it to a set
		// These will later be put into a the key field
		// of a hashmap
		for (ArrayList<Integer> i : uList) {
			for (Integer j : i) {
				PriceSet.add(j);
			}
		}

		// Store the the following in a HashMap with Key as Price and Value as
		// Item's Name
		for (Integer i : PriceSet) {
			for (int j = 0; j < ITEM.length; j++) {
				if (COST[j] == i) {
					if (ItemMap.containsKey(i)) {
						Set<String> existingP = new TreeSet<String>(
								ItemMap.get(i));
						existingP.add(ITEM[j]);
						ItemMap.put(i, existingP);
					} else {
						Set<String> newP = new TreeSet<String>();
						newP.add(ITEM[j]);
						ItemMap.put(i, newP);
					}
				}
			}
		}

		// Display the HashMap
		for (Map.Entry<Integer, Set<String>> e : ItemMap.entrySet()) {
			System.out.println(e.getKey() + ">>" + e.getValue());
		}

		System.out.println("The gift items that can be purchased for " + C
				+ "$ or less");

		// Prints all possible item names for a given price combination
		// Guava Library's Cartesian Product is used to perform this task
		/*
		 * The ItemMap hashmap will hold the following information: Key : Price,
		 * Value : Item's Name that has its price matching the key 2>>[a] 3>>[b]
		 * 4>>[c] 5>>[d] 6>>[e, f] 7>>[g] 8>>[h] 9>>[i]
		 * 
		 * 3 gift items that can be purchased for 14$ or less
		 * 
		 * [[a, b, h]] [[a, b, i]] [[a, c, g]] [[a, c, h]] [[a, c, i]] [[a, d,
		 * e], [a, d, f]] [[a, d, g]] [[a, d, h]]
		 */

		for (ArrayList<Integer> Price_combination : uList) {
			List<Set<String>> master_set = new ArrayList<Set<String>>();
			for (Integer price : Price_combination) {
				Set<String> temp_set_price = new TreeSet<String>();
				ArrayList<String> name_list = new ArrayList<String>(
						ItemMap.get(price));
				for (int i = 0; i < name_list.size(); i++) {
					temp_set_price.add(name_list.get(i));
				}
				Set<String> immutable_temp_set_price = ImmutableSet
						.copyOf(temp_set_price);
				master_set.add(immutable_temp_set_price);
			}
			Set<List<String>> cartesian_Product = Sets
					.cartesianProduct(master_set);

			for (List<String> final_Set : cartesian_Product) {
				// put the list into a set and print only those items whose size
				// is equal to that of No of gifts to be purchased
				Set<String> set_to_Print = new TreeSet<String>(final_Set);
				if (set_to_Print.size() == N) {
					System.out.println(set_to_Print);
				}
			}
		}

	}

	// Recursive function call that prints all combinations.
	//
	// @param current
	// The current location in the sorted COST array from which the
	// items combinations have to be tried.
	// @param selected
	// The current combination being tried.
	// @return Returns true if we need to investigate further for possible
	// combinations, false otherwise.
	//
	private static boolean canProceed(int current, ArrayList<Integer> selected) {
		int currentSumStatus = inRange(selected);
		if (selected.size() == N) {
			// We got the combination with the max given limit.
			if (currentSumStatus == 0) {
				System.out.println(selected);
				ArrayList<Integer> temp = new ArrayList<Integer>(selected);
				mList.add(temp);
				return true; // We need to keep checking for other combinations
								// of
								// size N
			} else if (currentSumStatus == -1) {
				return true; // Smaller, other numbers can make it bigger.
			} else if (currentSumStatus == 1) {
				return false; // Already big, no point scanning further.
			}
		} else if (selected.size() < N) {
			if (currentSumStatus == 0) {
				// We need to keep checking since there is still a chance for
				// other combinations
			} else if (currentSumStatus == -1) {
				// Smaller, other numbers can make it bigger.
			} else if (currentSumStatus == 1) {
				return false; // Already big, no point scanning further.
			}
		} else if (selected.size() > N) {
			return false;
		}

		for (int i = current; i < COST.length; i++) {
			selected.add(COST[i]);
			if (!canProceed(i + 1, selected)) {
				break;
			} else {
				selected.remove(selected.size() - 1);
			}
		}
		return true;
	}

	// Checks if the given array is within the range. Range is computed using C
	// and whatisApproximate
	//
	// @param selected
	// The array to be checked
	// @return -1 if less than range, +1 if more than range, 0 if within range.
	//
	private static int inRange(ArrayList<Integer> selected) {
		int sum = 0;
		for (Integer integer : selected) {
			sum += integer;
		}
		if (sum < C - whatIsApproximate) {
			return -1;
		} else if (sum > C + whatIsApproximate) {
			return 1;
		} else {
			return 0;
		}
	}

	// Call the Zappos API and stores the response in a file called
	// APIResponse.json
	// This JSON file is parsed and product information such as
	// 1. Price
	// 2. Product Name
	// are stored in an Array List
	// @param ZapposAPIURL - URL to perform the search
	// @param APIKey - Secret API key provided by Zappos

	static void getJSONResponse(String ZapposAPIURL, String APIKey) {
		String ZapposSearch = ZapposAPIURL;
		String myAPIKey = APIKey;
		JSONParser jsonParser = new JSONParser();
		try {
			URL ZapposURL = new URL(ZapposSearch + myAPIKey);
			HttpURLConnection conn = (HttpURLConnection) ZapposURL
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			// Write the API Response (JSON) to a file
			File JSON_file = new File("c:\\APIResponse.json");
			BufferedWriter bw = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				bw = new BufferedWriter(new FileWriter(JSON_file));

				String output;
				System.out.println("Reply from Zappos ...");
				while ((output = br.readLine()) != null) {
					bw.write(output);
					System.out.println(output);
				}
				bw.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// JSON Processing begins here
			Object obj = jsonParser
					.parse(new FileReader("g:\\APIResponse.json"));

			JSONObject MainLevel = (JSONObject) obj;
			JSONArray FirstLevel = (JSONArray) MainLevel.get("results");

			for (int i = 0; i < FirstLevel.size(); i++) {
				JSONObject SecondLevel = (JSONObject) FirstLevel.get(i);
				String productName = (String) SecondLevel.get("productName");
				ProductName.add(productName);
				String price = (String) SecondLevel.get("price");
				PriceList.add(price);
				String productURL = (String) SecondLevel.get("productUrl");
				System.out.println("Name :" + productName + " Price :" + price
						+ " URL Link :" + productURL);
			}

			conn.disconnect();

			System.out.println("Master Price List");
			System.out.println(PriceList);
			System.out.println("Master Item List");
			System.out.println(ProductName);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	// This function will pass the arraylist of prices whose format will be for example
	// $10.44 or $100.54 or $1,000.33 or $10,000.67. They will be converted to an
	// integer by using a regex. The above numbers will be stored as 10 or 100 or 1000
	// 10000 repectively.
	static void currencyFormatter(ArrayList<String> pList) {
		String regex = "^\\$?(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?\\.[0-9]{1,2}$";
		Pattern pattern = Pattern.compile(regex);
		int count = 0;
		for (String input_price : pList) {
			Matcher matcher = pattern.matcher(input_price);
			while (matcher.find()) {
				input_price = matcher.group(1).replace(",", "");
			}
			COST[count] = Integer.parseInt(input_price);
			count++;
		}

	}

	// This function will initialize the array with the all the item names
	static void initializeProductArray(ArrayList<String> pName) {
		int count = 0;
		for (String input_pname : pName) {
			ITEM[count] = input_pname;
			count++;
		}
	}
}
