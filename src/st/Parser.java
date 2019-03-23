package st;

import java.math.BigInteger;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Parser {
	public static final int INTEGER = 1;
	public static final int BOOLEAN = 2;
	public static final int STRING = 3;
	public static final int CHAR = 4;

	private OptionMap optionMap;

	public Parser() {
		optionMap = new OptionMap();
	}

	public void add(String option_name, String shortcut, int value_type) {
		optionMap.store(option_name, shortcut, value_type);
	}

	public void add(String option_name, int value_type) {
		optionMap.store(option_name, "", value_type);
	}

	public List<Integer> getIntegerList(String option) {
		List<Integer> result = new ArrayList<>();

		String value = getString(option);

		if (value == "") {
			return result;
		}

		String clean = value.replaceAll("[^\\d-]", " ");
		String[] array = clean.split(" "); // split string by non-digit except -
		System.out.print("original array length : " + array.length);

		for (int i = 0; i < array.length; i++) {
			if (array[i].contains("-")) {
				System.out.print("run here");
				char[] charArray = array[i].toCharArray();
				// 1-3
				int j = 0;
				while (j < array.length) {
					int temp = Character.getNumericValue(charArray[j]);
					int temp2 = Character.getNumericValue(charArray[j + 2]);

					if (charArray[j] == '-' && j == 0 && Character.isDigit(charArray[j + 1])) {
						temp = -(Character.getNumericValue(charArray[j + 1]));
						j = j + 2;
					}
					if ((charArray[j] == '-' && charArray[j + 1] == '-')) {
						temp2 = -(Character.getNumericValue(charArray[j + 2]));
					}
					int low = temp;
					int high = temp2;

					if (low < high) {
						while (low <= high) { // 1<3
							result.add(low); // add 1
							low++;
						}
						j = j + 2;
					} else if (low >= high) { // 3-1
						while (high <= low) {
							result.add(high);
							high++;

						}
						j = j + 2;
					}
				}
			} else {
				int a = Integer.parseInt(array[i]);
				result.add(a);

			}
		}
		return result;
	}

	public int getInteger(String option) {
		String value = getString(option);
		int type = getType(option);
		int result;
		switch (type) {
		case INTEGER:
			try {
				result = Integer.parseInt(value);
			} catch (Exception e) {
				try {
					new BigInteger(value);
				} catch (Exception e1) {
					result = 0;
				}
				result = 0;
			}
			break;
		case BOOLEAN:
			if (getBoolean(option) == false) {
				result = 0;
			} else {
				result = 1;
			}
			break;
		case STRING:
			int power = 1;
			result = 0;
			char c;
			for (int i = value.length() - 1; i >= 0; --i) {
				c = value.charAt(i);
				if (!Character.isDigit(c))
					return 0;
				result = result + power * (c - '0');
				power *= 10;
			}
			break;
		case CHAR:
			result = (int) getChar(option);
			break;
		default:
			result = 0;
		}
		return result;
	}

	public boolean getBoolean(String option) {
		String value = getString(option);
		boolean result;
		if (value.toLowerCase().equals("false") || value.equals("0") || value.equals("")) {
			result = false;
		} else {
			result = true;
		}
		return result;
	}

	public String getString(String option) {
		String result = optionMap.getValue(option);
		return result;
	}

	public char getChar(String option) {
		String value = getString(option);
		char result;
		if (value.equals("")) {
			result = '\0';
		} else {
			result = value.charAt(0);
		}
		return result;
	}

	public int parse(String command_line_options) {
		if (command_line_options == null) {
			return -1;
		}
		int length = command_line_options.length();
		if (length == 0) {
			return -2;
		}

		int char_index = 0;
		while (char_index < length) {
			char current_char = command_line_options.charAt(char_index);

			while (char_index < length) {
				current_char = command_line_options.charAt(char_index);
				if (current_char != ' ') {
					break;
				}
				char_index++;
			}

			boolean isShortcut = true;
			String option_name = "";
			String option_value = "";
			if (current_char == '-') {
				char_index++;
				current_char = command_line_options.charAt(char_index);
				if (current_char == '-') {
					isShortcut = false;
					char_index++;
				}
			} else {
				return -3;
			}
			current_char = command_line_options.charAt(char_index);

			while (char_index < length) {
				current_char = command_line_options.charAt(char_index);
				if (Character.isLetterOrDigit(current_char) || current_char == '_') {
					option_name += current_char;
					char_index++;
				} else {
					break;
				}
			}

			boolean hasSpace = false;
			if (current_char == ' ') {
				hasSpace = true;
				while (char_index < length) {
					current_char = command_line_options.charAt(char_index);
					if (current_char != ' ') {
						break;
					}
					char_index++;
				}
			}

			if (current_char == '=') {
				char_index++;
				current_char = command_line_options.charAt(char_index);
			}
			if ((current_char == '-' && hasSpace == true) || char_index == length) {
				if (getType(option_name) == BOOLEAN) {
					option_value = "true";
					if (isShortcut) {
						optionMap.setValueWithOptioShortcut(option_name, option_value);
					} else {
						optionMap.setValueWithOptionName(option_name, option_value);
					}
				} else {
					return -3;
				}
				continue;
			} else {
				char end_symbol = ' ';
				current_char = command_line_options.charAt(char_index);
				if (current_char == '\'' || current_char == '\"') {
					end_symbol = current_char;
					char_index++;
				}
				while (char_index < length) {
					current_char = command_line_options.charAt(char_index);
					if (current_char != end_symbol) {
						option_value = option_value + current_char;
						char_index++;
					} else {
						break;
					}
				}
			}

			if (isShortcut) {
				optionMap.setValueWithOptioShortcut(option_name, option_value);
			} else {
				optionMap.setValueWithOptionName(option_name, option_value);
			}
			char_index++;
		}
		return 0;
	}

	private int getType(String option) {
		int type = optionMap.getType(option);
		return type;
	}

	@Override
	public String toString() {
		return optionMap.toString();
	}

}
