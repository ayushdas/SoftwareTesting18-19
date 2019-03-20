import static org.junit.Assert.*;




import org.junit.Test;


import st.OptionMap;
import st.Parser;

import org.junit.Before;

public class Task2_Coverage {

	private Parser parser;
	private OptionMap map;
	

	@Before
	public void set_up() {
		parser = new Parser();
		map = new OptionMap();

	}

	/*
	 * Test getType() in optionMap
	 */
	@Test
	public void getType() {
		map.store("output", "o", Parser.STRING);
		assertEquals(map.getType("o"), 3);
		assertEquals(map.getType("y"), 0); // option does not exist

	}

	/*
	 * Test toString in optionMap
	 */
	@Test
	public void toStringTest() {
		
		parser.add("output", "o", Parser.STRING);
		parser.parse("--output=output.txt");
		assertEquals(parser.toString(),
				"OptionMap [options=\n\t{name=output, shortcut=o, type=3, value=output.txt}\n]");
	}
	
	/*
	 * Test getInteger in Parser
	 * 
	 */
	@Test
	public void getInteger() {
		// test integer too big
		parser.add("output", "o", Parser.INTEGER);
		parser.parse("--output= 100!");
		assertEquals(parser.getInteger("output"), 0);
		
		Integer bigInt = Integer.MAX_VALUE;
		parser.add("output",Parser.INTEGER);
		parser.parse("--output = " + bigInt);
		assertEquals(parser.getInteger("output"),0);
	
		// test boolean
		parser.add("output", "o", Parser.BOOLEAN);
		parser.parse("--output=true");
		assertEquals(parser.getInteger("output"), 1);

		parser.add("output", "o", Parser.BOOLEAN);
		parser.parse("--output=false");
		assertEquals(parser.getInteger("output"), 0);

		// test string
		parser.add("output", "o", Parser.STRING);
		parser.parse("--output=output.txt");
		assertEquals(parser.getInteger("output"), 0);

		parser.add("output", "o", Parser.STRING);
		parser.parse("-- output =5abc");
		assertEquals(parser.getInteger("output"), 0);

		parser.add("output", "o", Parser.STRING);
		parser.parse("-- output = ");
		assertEquals(parser.getInteger("output"), 0);
		
	
		parser.add("output",Parser.STRING);
		parser.parse("--output=2");
		assertEquals(parser.getInteger("output"),2);
	

		// test char
		parser.add("output", "o", Parser.CHAR);
		parser.parse("--output=o");
		assertEquals(parser.getInteger("output"), 111);
			
		// test default
		parser.add("output" , "o", 5);
		assertEquals(parser.getInteger("o"), 0);
	}

	/*
	 * test parse() in Parser
	 */
	@Test
	public void parse() {
		parser.add("output", "o", Parser.CHAR);
		assertEquals(parser.parse(null), -1);
		assertEquals(parser.parse(""), -2);
		assertEquals(parser.parse("--output =output"), 0);

		parser.add("output", "o", Parser.STRING);
		assertEquals(parser.parse("--output=_"), 0);
		
		// test command that has wrong type
		parser.add("output", "O", Parser.CHAR);
		assertEquals(parser.parse("-O"), -3);
		assertEquals(parser.parse("output= abc-"), -3);

		// test command with space
		parser.add("output", "o", Parser.CHAR);
		assertEquals(parser.parse("--output= output  "), -3);

		// test command without - 
		parser.add("output", "o", Parser.CHAR);
		assertEquals(parser.parse("output"), -3);
		
		
		// test command that has only space
		parser.add("output", Parser.INTEGER);
		assertEquals(parser.parse("--output=      "), -3);
		
		//test faulty command
		parser.add("output", Parser.STRING);
		assertEquals(parser.parse("--$$$= output" ), -3);
		
		
		parser.add("output_",Parser.STRING);
		assertEquals(parser.parse("--output_= output"), -3);
		

		parser.add("output_",Parser.STRING);
		assertEquals(parser.parse("--output_ "), -3);
		
		parser.add("output","O", Parser.BOOLEAN);
		assertEquals(parser.parse("--output -0"),-3);
	
	

	}

	// Checking basic parser functionality
	@Test
	public void example_test() {
		parser.add("output", "o", Parser.STRING);
		parser.parse("--output=output.txt");
		assertEquals(parser.getString("o"), "output.txt");
	}

	/* Checking parser types */
	@Test
	public void check_datatypes_in_add_with_shortcut() {
		parser.add("output", "o", Parser.STRING);
		parser.parse("--output=output.txt");
		assertEquals(parser.getString("output"), "output.txt");
		parser.parse("--output output2.txt");
		assertEquals(parser.getString("output"), "output2.txt");
		assertEquals(parser.getString("o"), "output2.txt");

		parser.add("output", "o", Parser.INTEGER);
		parser.parse("--output=90");
		assertEquals(parser.getInteger("output"), 90);
		parser.parse("--output 100");
		assertEquals(parser.getInteger("output"), 100);
		assertEquals(parser.getInteger("o"), 100);

		parser.add("output", "o", Parser.BOOLEAN);
		parser.parse("--output=true");
		assertEquals(parser.getBoolean("output"), true);
		parser.parse("--output false");
		assertEquals(parser.getBoolean("output"), false);
		assertEquals(parser.getBoolean("o"), false);

		parser.add("output", "o", Parser.CHAR);
		parser.parse("--output=a");
		assertEquals(parser.getChar("output"), 'a');
		parser.parse("--output b");
		assertEquals(parser.getChar("output"), 'b');
		assertEquals(parser.getChar("o"), 'b');
	}

	@Test
	public void check_datatypes_in_add_without_shortcut() {
		parser.add("output", Parser.STRING);
		parser.parse("--output=output.txt");
		assertEquals(parser.getString("output"), "output.txt");
		parser.parse("--output output2.txt");
		assertEquals(parser.getString("output"), "output2.txt");

		parser.add("output", Parser.INTEGER);
		parser.parse("--output=90");
		assertEquals(parser.getInteger("output"), 90);
		parser.parse("--output 100");
		assertEquals(parser.getInteger("output"), 100);

		parser.add("output", Parser.BOOLEAN);
		parser.parse("--output=true");
		assertEquals(parser.getBoolean("output"), true);
		parser.parse("--output false");
		assertEquals(parser.getBoolean("output"), false);

		parser.add("output", Parser.CHAR);
		parser.parse("--output=a");
		assertEquals(parser.getChar("output"), 'a');
		parser.parse("--output b");
		assertEquals(parser.getChar("output"), 'b');

	}

	/* ** Adding options with a shortcut ** */
	/*
	 * Specification 1: Adding option with same name as existing option will
	 * override previously defined option
	 */
	@Test
	public void overriding_option_name_test() {
		parser.add("output", "o", Parser.STRING);
		parser.parse("--output=input1.txt");
		assertEquals(parser.getString("output"), "input1.txt");
		parser.add("output", "new_o", Parser.STRING);
		parser.parse("--output=input2.txt");
		assertEquals(parser.getString("output"), "input2.txt");
		assertNotEquals(parser.getString("output"), "input1.txt");
		assertEquals(parser.getString("new_o"), "input2.txt");
		assertNotEquals(parser.getString("new_o"), "input1.txt");
		assertNotEquals(parser.getString("o"), "input1.txt");
	}

	/* Option names are case sensitive */
	@Test
	public void case_sensitive_option_names_test() {
		parser.add("Output", Parser.STRING);
		parser.add("OuTPut", Parser.STRING);
		parser.add("OuTPUt", Parser.STRING);
		parser.parse("--Output=input1.txt --OuTPut=input2.txt --OuTPUt=input1.jpeg");
		assertEquals(parser.getString("Output"), "input1.txt");
		assertEquals(parser.getString("OuTPut"), "input2.txt");
		assertEquals(parser.getString("OuTPUt"), "input1.jpeg");
	}

	/* Short cuts are case sensitive */
	@Test
	public void case_sensitive_shortcut_names_test() {
		parser.add("Output", "ot", Parser.STRING);
		parser.add("Output1", "oT", Parser.STRING);
		parser.add("Output2", "Ot", Parser.STRING);
		parser.add("Output3", "OT", Parser.STRING);
		parser.parse("-ot=input1.txt " + "-oT=input2.txt -Ot=input3.txt -OT=input4.txt");
		assertEquals(parser.getString("ot"), "input1.txt");
		assertEquals(parser.getString("oT"), "input2.txt");
		assertEquals(parser.getString("Ot"), "input3.txt");
		assertEquals(parser.getString("OT"), "input4.txt");
	}

	/*
	 * An option can have a shortcut that is the same as the name of another option
	 */
	@Test
	public void shortcut_option_name_swap_test() {
		parser.add("output", "o", Parser.STRING);
		parser.add("o", "output", Parser.STRING);
		parser.parse("--output=input1.txt --o=input2.txt");
		assertEquals(parser.getString("output"), "input1.txt");
		assertEquals(parser.getString("o"), "input2.txt");
	}

	@Test
	public void shortcut_option_name_swap_test_with_different_types() {
		parser.add("o", "output", Parser.INTEGER);
		parser.add("output", "o", Parser.STRING);
		parser.parse("-o=output.txt -output=100");
		assertEquals(parser.getString("output"), "output.txt");
		assertEquals(parser.getInteger("o"), 100);
	}

	/* Check boolean case insensitive True/False */
	@Test
	public void check_boolean_true_or_false() {
		parser.add("output1", "o1", Parser.BOOLEAN);
		parser.add("output2", "o2", Parser.BOOLEAN);
		parser.add("output3", "o3", Parser.BOOLEAN);
		parser.add("output4", "o3", Parser.BOOLEAN);
		parser.parse("--output1=True");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("--output2=trUe");
		assertEquals(parser.getBoolean("output2"), true);
		parser.parse("--output3=false");
		assertEquals(parser.getBoolean("output3"), false);
		parser.parse("--output4=FalSE");
		assertEquals(parser.getBoolean("output4"), false);
	}

	@Test
	public void check_boolean_false_conditions() {
		parser.add("output1", "o1", Parser.BOOLEAN);
		parser.parse("--output1=0");
		assertEquals(parser.getBoolean("output1"), false);
		parser.parse("--output1=false");
		assertEquals(parser.getBoolean("output1"), false);
		parser.add("output1", "o1", Parser.BOOLEAN);
		assertEquals(parser.getBoolean("output1"), false);
	}

	@Test
	public void check_boolean_true_conditions() {
		parser.add("output1", "o1", Parser.BOOLEAN);
		parser.parse("--output1");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("-o1");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("--output1=1");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("--output1=true");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("--output1=100");
		assertEquals(parser.getBoolean("output1"), true);
		parser.parse("--output1=-1");
		assertEquals(parser.getBoolean("output1"), true);
	}

	@Test
	/* Retrieve Information Test */
	public void order_of_search_test() {
		parser.add("output1", "o1", Parser.BOOLEAN);
		parser.add("o1", "o11", Parser.BOOLEAN);
		parser.parse("output1=true o1=false");
		assertNotEquals(parser.getBoolean("o1"), true);
		assertEquals(parser.getBoolean("o1"), false);
	}

	@Test
	public void default_return_types() {

		parser.add("output1", Parser.BOOLEAN);
		assertEquals(parser.getBoolean("output1"), false);
		parser.add("output1", Parser.STRING);
		assertEquals(parser.getString("output1"), "");
		parser.add("output1", Parser.CHAR);
		assertEquals(parser.getChar("output1"), '\0');
		parser.add("output1", Parser.INTEGER);
		assertEquals(parser.getInteger("output1"), 0);
	}

	@Test
	/* Parser.parse tests */
	/* Parser.parse returns 0 for successful parse else other values */
	public void check_parser_return_types() {
		parser.add("output1", Parser.STRING);
		parser.add("output2", Parser.STRING);
		int result = parser.parse("--output1=Hello --output2=World");
		assertEquals(result, 0);
		result = parser.parse("This is not a valid parse");
		assertNotEquals(result, 0);
	}

	@Test
	/* User Can use several quotation marks */
	public void several_quotation_marks() {
		parser.add("output1", "o1", Parser.STRING);
		parser.add("output2", "o2", Parser.STRING);
		parser.add("output3", "o3", Parser.STRING);
		parser.parse("-o1='Hello' -o2=World -o3=\"!\"");
		assertEquals(parser.getString("output1"), "Hello");
		assertEquals(parser.getString("output2"), "World");
		assertEquals(parser.getString("output3"), "!");
	}

	@Test
	/* User Can use quotation marks to decorate values */
	public void quotation_marks_to_decorate_values() {
		parser.add("output1", "o1", Parser.STRING);
		int result = parser.parse("-o1='value=\"123\"'");
		assertEquals(result, 0);
		assertEquals(parser.getString("output1"), "value=\"123\"");
		result = parser.parse("-o1 'value=\"123\"'");
		assertEquals(result, 0);
		assertEquals(parser.getString("output1"), "value=\"123\"");
	}

	@Test
	/* Value is from the last assignment */
	public void value_is_from_last_assignment() {
		parser.add("output1", "o1", Parser.STRING);
		parser.parse("-o1='value=\"123\"' -o1=234");
		parser.parse("-o1 parsed");
		assertEquals(parser.getString("output1"), "parsed");
	}

	@Test
	/* User need not provide value for every option */
	public void value_need_not_provided_for_every_option() {
		parser.add("output1", "o1", Parser.STRING);
		parser.add("output2", "o2", Parser.STRING);
		parser.add("output3", "o3", Parser.STRING);
		parser.parse("-o1='Hello' -o2=World");
		assertEquals(parser.getString("output1"), "Hello");
		assertEquals(parser.getString("output2"), "World");
		assertEquals(parser.getString("output3"), ""); // The default value
	}

	@Test
	/* Method .parse() can be used multiple times */
	public void method_used_multiple_times() {
		parser.add("output1", "o1", Parser.STRING);
		parser.add("output2", "o2", Parser.INTEGER);
		parser.add("output3", "o3", Parser.STRING);
		parser.parse("-o1=Hello");
		parser.parse("-o2=123");
		parser.parse("-o3=World");
		assertEquals(parser.getString("output1"), "Hello");
		assertEquals(parser.getInteger("output2"), 123);
		assertEquals(parser.getString("output3"), "World");
	}

}
