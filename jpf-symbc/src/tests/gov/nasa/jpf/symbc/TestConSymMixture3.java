/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package gov.nasa.jpf.symbc;

import org.junit.Test;

public class TestConSymMixture3 extends InvokeTest {

	private static final String SYM_METHOD = "+symbolic.method=gov.nasa.jpf.symbc.TestIFLEAndIADDAndConSymMixture3.test(con#sym#sym)";
	private static final String[] JPF_ARGS = { INSN_FACTORY, SYM_METHOD };

	public static void main(String[] args) {
		runTestsOfThisClass(args);
	}

	@Test
	public void mainTest() {
		if (verifyNoPropertyViolation(JPF_ARGS)) {
			int a = 3;
			int b = 8;
			test(a, b, a);
		}
	}

	/*
	 * test concrete = symbolic
	 * (con#sym#sym)
	 */
	public void test(int x, int y, int z) {
		System.out.println("Testing TestConSymMixture3");
		x = z;
		y = z + x;
		if (x < y)
			System.out.println("branch FOO1");
		else
			System.out.println("branch FOO2");
		if (z > 0)
			System.out.println("branch BOO1");
		else
			System.out.println("branch BOO2");
	}
}
