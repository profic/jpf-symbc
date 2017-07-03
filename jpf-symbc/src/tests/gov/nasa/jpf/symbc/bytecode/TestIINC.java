package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.KernelState;
import gov.nasa.jpf.vm.SingleProcessVM;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVMStackFrame;
import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestIINC extends TestJPF {
	private class MockThreadInfo extends ThreadInfo {
		public MockThreadInfo(VM vm) {
			super(vm, 0, null);
		}
		
		@Override
		public void init(VM vm) {
		}
	}
	
	private class MockSystemState extends SystemState {
		public MockSystemState() {
			super();
		}
	}
	
	private class MockVM extends SingleProcessVM {
		public MockVM() {
			super();
			this.ss = new MockSystemState();
			// initFields(null);
		}
	}

	@Test
	public void executeWithConcrete() {
		IINC iinc;
		VM vm = new MockVM();
		ThreadInfo threadInfo = new MockThreadInfo(vm);
		Instruction dummyInstruction = new Instruction() {
			@Override
			public Instruction execute(ThreadInfo threadInfo) {
				return null;
			}
			
			@Override
			public int getByteCode() {
				return 0;
			}
		};
		threadInfo.setNextPC(dummyInstruction);
		StackFrame stackFrame = new JVMStackFrame(null);
		threadInfo.pushFrame(stackFrame);
	}
}
