import static org.junit.Assert.*;

import org.junit.Test;

import st.Parser;

import org.junit.Before;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

public class Task3_TDD_1 {
	private Parser parser;
	@Before
	public void set_up() {
		parser = new Parser();
	}
	
	/* Spec 1: The order of search is full name of options followed by shortcut names */
	@Test // Checking for full name first test
	public void test_order_of_search() {
		parser.add("output", "o", Parser.STRING);
		parser.add("output2", "output", Parser.STRING);
		parser.parse("--output=1,2,3,4,5");
		parser.parse("-output=1,2,3,4,5,6,7,8,9,10");
		List<Integer> list1 = Arrays.asList(1,2,3,4,5);
		List<Integer> list2 = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		List<Integer> result = parser.getIntegerList("output");
		assertEquals(list1, result);
		result = parser.getIntegerList("output2");
		assertEquals(list2, result);
	}
	@Test // Checking for full name and then option shortcut name
	public void test_order_of_search_option_shortcut() {
		parser.add("output1", "o1", Parser.STRING);
		parser.add("output2", "o2", Parser.STRING);
		parser.parse("-o1=1,2,3,4,5");
		parser.parse("-o2=1,2,3,4,5,6,7,8,9,10");
		List<Integer> list1 = Arrays.asList(1,2,3,4,5);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	/* Spec 2: If option is not provided a value then empty list is returned */
	
	@Test // Checking for full name and then option shortcut name
	public void empty_list_test() {
		parser.add("output1", "o1", Parser.STRING);
		parser.add("output2", "o2", Parser.STRING);
		parser.parse("-o1 -o2=1,2,3");
		List<Integer> list1 = Arrays.asList();
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}

	/*Spec 3: Non-number characters except hyphen (-) can be used as separators including commas,
dots, spaces etc */
	
	@Test // Separator with a space
	public void valid_list_seperator_test_1() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1,2 3");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	@Test // Separator with a .
	public void valid_list_seperator_test_2() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1,2.3");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test // Separator with mixed symbols
	public void valid_list_seperator_test_3() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1={}1<2>3({)");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test // Separator with mixed symbols 2
	public void valid_list_seperator_test_4() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=;lldl1,2,3mdkmd");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	/*Spec 4: Include the range */
	@Test // Checking the range (-) operator
	public void check_range() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1-3");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=3-1");
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	/*Spec 5: Include the range with negative numbers*/
	@Test // Checking the range (-) operator with negative numbers
	public void check_range_negative_numbers_test_1() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1--3");
		List<Integer> list1 = Arrays.asList(-3,-2,-1);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-3--1");
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test // Checking the range (-) operator with negative and positive numbers
	public void check_range_negative_numbers_test_2() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1-3");
		List<Integer> list1 = Arrays.asList(-1,0,1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=3--1");
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test
	//Spec 6: Hyphens cannot be used as a suffix. ​3-​ , for instance, 
	//is invalid and an empty list should be returned.
	public void suffix_test() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1-3-");
		List<Integer> list1 = Arrays.asList();
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=3--1-");
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	// Empty list is returned in the these invalid cases of the integer list
	// invalid cases result in empty list being returned by the function
	@Test
	public void continuous_hyphens() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1-2-3");
		List<Integer> list1 = Arrays.asList(); // Empty list
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	@Test
	public void invalid_character_in_range() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1{--5");
		List<Integer> list1 = Arrays.asList(); // Empty list
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	@Test
	public void invalid_character_in_range_2() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1{--5");
		List<Integer> list1 = Arrays.asList(); // Empty list
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	// Checking integer list with a multiple combination of valid 
	@Test // repeated bound should result in repeated numbers
	public void reapeated_bound() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1-2 2-3");
		List<Integer> list1 = Arrays.asList(1,2,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1-2;3-2");
		list1 = Arrays.asList(1,2,2,3);
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test // repeated bound should result in repeated numbers some different cases
	public void reapeated_bound_with_negative_numbers() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1-2 2-3");
		List<Integer> list1 = Arrays.asList(-1,0,1,2,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1-2;3--2");
		list1 = Arrays.asList(-2,-1,0,1,1,2,2,3);
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	@Test // Long range test
	public void long_range_test() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=-1-2 3-5");
		List<Integer> list1 = Arrays.asList(-1,0,1,2,3,4,5);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
	
	@Test // Double & Single quotes should not be an issue 
	//while being used as separator
	public void double_quote_sperator_test() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=\"1\"2\"3\"");
		List<Integer> list1 = Arrays.asList(1,2,3);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1='1'2'3'");
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}
		
	@Test // Splitting parse across several line should still generate the same result 
	public void split_parse() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1,2,3,4,5");
		List<Integer> list1 = Arrays.asList(1,2,3,4,5);
		List<Integer> result = parser.getIntegerList("o1");
		assertEquals(list1, result);
		// Only the most recent value must lie in the option
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1=1,2,3");
		parser.parse("-o1=4,5");
		list1 = Arrays.asList(4,5);
		result = parser.getIntegerList("o1");
		assertEquals(list1, result);
	}	

}
