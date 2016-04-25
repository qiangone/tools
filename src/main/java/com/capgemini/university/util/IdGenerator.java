package com.capgemini.university.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IdGenerator {

	private static IdGenerator instance;

	private IdGenerator() {

	}

	public static IdGenerator getInstance() {
		if (instance == null) {
			instance = new IdGenerator();
		}
		return instance;
	}

	public static void main(String[] args) throws InterruptedException {
		// for (int i = 0; i < 10000; i++) {
		// System.out.println(T.getOrderNo(1));
		// Thread.sleep(1000);
		// }
		// UUID uuid = UUID.randomUUID();
		// System.out.println(uuid);

		// Random r = new Random(10);
		// for (int i = 0; i < 100; i++) {
		// System.out.println(r.nextInt(100));
		// }

		IdGenerator a = IdGenerator.getInstance();
		String str = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		System.out.println(str);
		System.out.println(a.getOrderMasterNo(1));
	}

	/**
	 * 生成订单编号
	 * 
	 * @return
	 */
	public synchronized String getOrderMasterNo(Integer userId) {
		String str = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String fixRandom = getFixLenthString(4);
		String orderNo = str + fixRandom + userId;
		return orderNo + "";
	}

	private static String getFixLenthString(int strLength) {

		Random rm = new Random();

		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

		String fixLenthString = String.valueOf(pross);

		return fixLenthString.substring(1, strLength + 1);
	}

}
