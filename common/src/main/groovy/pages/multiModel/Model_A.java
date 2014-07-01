package org.graphwalker.examples.modelAPI;

import java.io.File;

import org.graphwalker.generators.PathGenerator;

public class Model_A extends org.graphwalker.multipleModels.ModelAPI {

  public Model_A(File model, boolean efsm, PathGenerator generator, boolean weight) {
    super(model, efsm, generator, weight);
  }


  /**
   * This method implements the Edge 'e_A'
   * 
   */
  public void e_A() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Edge 'e_B'
   * 
   */
  public void e_B() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_A'
   * 
   */
  public void v_A() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_B'
   * 
   */
  public void v_B() throws InterruptedException {
    Thread.sleep(500);
  }
}
