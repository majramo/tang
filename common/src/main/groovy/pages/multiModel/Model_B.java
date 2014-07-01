package org.graphwalker.examples.modelAPI;

import java.io.File;
import org.graphwalker.generators.PathGenerator;

public class Model_B extends org.graphwalker.multipleModels.ModelAPI {

  public Model_B(File model, boolean efsm, PathGenerator generator, boolean weight) {
    super(model, efsm, generator, weight);
  }


  /**
   * This method implements the Edge 'e_B'
   * 
   */
  public void e_B() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Edge 'e_C'
   * 
   */
  public void e_C() throws InterruptedException {
    Thread.sleep(500);
  }

  /**
   * This method implements the Edge 'e_D'
   * 
   */
  public void e_D() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_B'
   * 
   */
  public void v_B() throws InterruptedException {
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
   * This method implements the Vertex 'v_D'
   * 
   */
  public void v_D() throws InterruptedException {
    Thread.sleep(500);
  }


}

