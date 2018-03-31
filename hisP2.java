

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Xunhua Wang (wangxx@jmu.edu), Nicholas Hawkins, Gunnar Kane
 * @date 09/25/2014; further refined on 02/20/2016 and 09/24/2016
 *
 * Used with permission from the instructor for CS 457 - Project 2
 *
 * NOTE: Do not redistribute this code. JMU Honor Code applies!
 */

public class hisP2 {
	
	public void crack (int start, int end) {

		byte[] myguessedkey = new byte[16];
		for (int i = 0; i < 16; i ++) myguessedkey[i] = (byte) 0x00; // This may be unnecessary but to make sure ...
		myguessedkey[15] = (byte) 0x03;

		for (int i = start; i <= end; i++) {
			for (int j = 0; j <= 255; j++) {
				for (int k = 0; k <= 255; k++) {
					for (int x = 0; x <= 255; x++) {
						for (int y = 0x60; y <= 0xe0; y+=0x80) { // Why does y start from 0x60 and increment by 0x80? Think!
							myguessedkey[0] = (byte) i;
	 						myguessedkey[1] = (byte) j;
	 						myguessedkey[2] = (byte) k; 	
							myguessedkey[3] = (byte) x;
	 						myguessedkey[4] = (byte) y;

							//
							// In my test run, print out myguessedkey to make sure that it is correct
							// This line of code prints out and slows things down	
							//
							//	TODO: the following two lines should be commented out when you start running your code
							//
							//for (byte ki : myguessedkey) System.out.print (String.format("%02X", ki & 0xff));
							//System.out.println ("");

							//
							// Next, we will test whether this guessed key is good or not
							// TODO: YOU need to fill in the details here
							//
							boolean isCorrectKey = testOneKey (myguessedkey);

							if (isCorrectKey) return;
						}
					}
				}
			}
		}
	}

	//
	// Method UNFINISHED
	//
	public boolean testOneKey (byte[] inKey) {
		//
		// Is this key correct? How to find out?
		//

		//
		// Step 0. Prepare the IV and ciphertext blocks; TODO: you will need to change the following values accordingly.
		//
		String ivStr = "A05E7122460B78654C36A566414520CF";
		String c1Str = "0138BBC9F2E4E75D111DDB5BFFAE1246";
		String c2Str = "FB52EA234FFBB029D02AED90C46A73DA";
		String c3Str = "41DB7697BBAEA7518E41BF6C2E84BF66";
		String c4Str = "DD84A48FB784A68B34E75D0E47DA05E0";

		byte[] iv = hexStringToByteArray (ivStr);
		byte[] c1 = hexStringToByteArray (c1Str);
		byte[] c2 = hexStringToByteArray (c2Str);
		byte[] c3 = hexStringToByteArray (c3Str);
		byte[] c4 = hexStringToByteArray (c4Str);
		
		// NICK added variables
		//int numOfCiphertextBlocks = cipherText.length / 16 - 1; 
		byte[] cleartextBlock1 = new byte[16];
		byte[] cleartextBlock2 = new byte[16];
		byte[] cleartextBlock3 = new byte[16];
		byte[] cleartextBlock4 = new byte[16];

		//
		// Step 1. Prepare for the AES round keys
		//
		Object aesRoundKeys = null;

		try {
			aesRoundKeys = Rijndael_Algorithm.makeKey(Rijndael_Algorithm.DECRYPT_MODE, inKey);
		} catch (Exception ex) {
			ex.printStackTrace ();

			return false;
		}

		//
		// Step 2. Now decrypt ciphertext block 1 _and_ XOR with IV; UNFINISHED
		//	

		byte[] returnArray = Rijndael_Algorithm.blockDecrypt2(c1, 0, aesRoundKeys);
		// TODO: XOR with IV and test whether the cleartext block is good or not. If not, return false
		//	Otherwise, continue
		//

		
		for (int j = 0; j < 16; j++)
			cleartextBlock1[j] = (byte) (returnArray[j] ^ iv[j]);
		
		String recoveredString = new String (cleartextBlock1);
		
		if (!withinASCII(recoveredString))
			return false;

		
		//
		// Step 3. Now decrypt ciphertext block 2 _and_ XOR with c1; UNFINISHED
		//	
		byte[] returnArray2 = Rijndael_Algorithm.blockDecrypt2(c2, 0, aesRoundKeys);
		// TODO: XOR with c1 and test whether the cleartext block is good or not. If not, return false
		//	Otherwise, continue
		//
		

		for (int j = 0; j < 16; j++)
			cleartextBlock2[j] = (byte) (returnArray2[j] ^ c1[j]);
		
		String recoveredString2 = new String (cleartextBlock2);
		
		if (!withinASCII(recoveredString2))
			return false;
		
		
		//
		// Step 4. Now decrypt ciphertext block 3 _and_ XOR with c2; UNFINISHED
		//	
		byte[] returnArray3 = Rijndael_Algorithm.blockDecrypt2(c3, 0, aesRoundKeys);
		// TODO: XOR with c2 and test whether the cleartext block is good or not. If not, return false
		//	Otherwise, continue
		//
		

		for (int j = 0; j < 16; j++)
			cleartextBlock3[j] = (byte) (returnArray3[j] ^ c2[j]);
		
		String recoveredString3 = new String (cleartextBlock3);
		
		if (!withinASCII(recoveredString3))
			return false;

		//
		// Step 5. Now decrypt ciphertext block 4 _and_ XOR with c3; UNFINISHED
		//	
		byte[] returnArray4 = Rijndael_Algorithm.blockDecrypt2(c4, 0, aesRoundKeys);
		// TODO: XOR with c3 and test whether the cleartext block is good or not. If not, return false
		//	Otherwise, found the key; print the key and all four plaintext blocks to a file
		//
		

		for (int j = 0; j < 16; j++)
			cleartextBlock4[j] = (byte) (returnArray4[j] ^ c3[j]);
		
		String recoveredString4 = new String (cleartextBlock4);
		
		try {
			if (!withinASCII(recoveredString4))
				return false;
			else
			{
				File file = new File("out.txt");
				BufferedWriter out;
				out = new BufferedWriter(new FileWriter(file, true));
				out.write("KEY: " + Arrays.toString(inKey) + "\n");
				out.write("Cleartext: " + recoveredString + recoveredString2 + recoveredString3 + recoveredString4 + "\n");
				out.close();
				
				System.out.println("KEY: " + Arrays.toString(inKey));
				System.out.println("Cleartext: " + recoveredString + recoveredString2 + recoveredString3 + recoveredString4 + "\n");
				
			} 
		}
		catch (IOException e) 
		{
				e.printStackTrace();
				return false;
		}

		
		//
		// The following line should be removed after you complete the code
		//
		

				
		return false;
		
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
	
	public static boolean withinASCII(String s) {
		for (int ii = 0; ii < s.length(); ii++)
		{
			if (!(s.charAt(ii) >=32 && s.charAt(ii) < 127))
			{
				return false;
			}
		}
		
		return true;
	}

	public static void main (String args[]) {
		if (args.length < 2) {
			System.out.println ("Use java AESChallengeCrackerWithFiveLoops start end");
			return;
		}

		try {
			int start = Integer.parseInt (args[0]);
			int end = Integer.parseInt (args[1]);

			hisP2 acc = new hisP2 ();

			acc.crack (start, end);
		} catch (Exception ex) {
			ex.printStackTrace ();
		}
	}
}
