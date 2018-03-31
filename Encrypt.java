package p2;

/**
 * @author Nicholas Hawkins, Gunnar Kane
 * @date 09/27/2016
 *
 * Used for CS 457 - Project 2 to encrypt plaintext with the found AES128 key
 *
 */


import java.util.Arrays;

public class Encrypt {
	public static void main(String args[]) {
		try {
			byte[] inKey = new byte[]{66,11,-61,127,96,0,0,0,0,0,0,0,0,0,0,3};
			byte[] cbcIV = new byte[16];
			String iv = "67c720b72a53b4bf9733732fad997119";
			
			cbcIV = hexStringToByteArray(iv);
			
			System.out.println("AES-128 key is " + convertToString(inKey));

			// populate the plaintext
			String textString = "Transfer fifty thousand dollars from my bank account to Eve Wang"; // exactly
																	// 32 bytes,
																	// two
																	// blocks of
																	// data

			byte[] inText = textString.getBytes(); // This will return the ASCII
													// encoding of the
													// characters
			int numOfBlocks = inText.length / 16; // Each AES block has 16 bytes

			Object roundKeys = Rijndael_Algorithm.makeKey(Rijndael_Algorithm.ENCRYPT_MODE, inKey); // This
																									// sets
																									// up
																									// the
																									// key

			// Now, we are ready and let's start the business
			System.out.println(System.getProperty("line.separator") + "Encrypting ......");
			System.out.println("Plaintext is " + textString);
			System.out.println("IV is " + convertToString(cbcIV));

			byte[] cipherText = new byte[cbcIV.length + inText.length];
			byte[] feedback = Arrays.copyOf(cbcIV, cbcIV.length);
			for (int i = 0; i < 16; i++)
				cipherText[i] = cbcIV[i];
			byte[] currentBlock = new byte[16];

			for (int i = 0; i < numOfBlocks; i++) {
				for (int j = 0; j < 16; j++)
					currentBlock[j] = (byte) (inText[i * 16 + j] ^ feedback[j]); // CBC
																					// feedback

				byte[] thisCipherBlock = Rijndael_Algorithm.blockEncrypt2(currentBlock, 0, roundKeys);

				feedback = Arrays.copyOf(thisCipherBlock, thisCipherBlock.length);

				for (int j = 0; j < 16; j++)
					cipherText[(i + 1) * 16 + j] = thisCipherBlock[j];
			}

			System.out.println("Ciphertext (including IV) is " + convertToString(cipherText));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String convertToString (byte[] data) {
		  char[] _hexArray = {'0', '1', '2', '3', '4', '5','6', '7', '8',
		       '9', 'A', 'B', 'C', 'D', 'E', 'F'};

		  StringBuffer sb = new StringBuffer();

		  for (int i=0; i <data.length; i++) {
		   sb.append("" + _hexArray[(data[i] >> 4) & 0x0f] + _hexArray[data[i] & 0x0f]);
		  }

		  return sb.toString();
		 }
	
	// 
	// The following method is copied from Medovar and Sharp. Thank them for the code when you get a chance
	// 
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}

		return data;
	}
}
