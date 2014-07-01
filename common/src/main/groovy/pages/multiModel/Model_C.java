package org.graphwalker.examples.modelAPI;

import java.io.File;
import org.graphwalker.generators.PathGenerator;

public class Model_C extends org.graphwalker.multipleModels.ModelAPI {

  public Model_C(File model, boolean efsm, PathGenerator generator, boolean weight) {
    super(model, efsm, generator, weight);
  }


  /**
   * This method implements the Edge 'e_C'
   * 
   */
  public void e_C() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Edge 'e_E'
   * 
   */
  public void e_E() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_C'
   * 
   */
  public void v_C() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_E'
   * 
   */
  public void v_E() throws InterruptedException {
    Thread.sleep(500);
  }


}

