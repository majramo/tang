package org.graphwalker.examples.modelAPI;

import java.io.File;
import org.graphwalker.generators.PathGenerator;

public class Model_D extends org.graphwalker.multipleModels.ModelAPI {

  public Model_D(File model, boolean efsm, PathGenerator generator, boolean weight) {
    super(model, efsm, generator, weight);
  }


  /**
   * This method implements the Edge 'e_D'
   * 
   */
  public void e_D() throws InterruptedException {
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
   * This method implements the Edge 'e_F'
   * 
   */
  public void e_F() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_D'
   * 
   */
  public void v_D() throws InterruptedException {
    Thread.sleep(500);
  }


  /**
   * This method implements the Vertex 'v_F'
   * 
   */
  public void v_F() throws InterruptedException {
    Thread.sleep(500);
  }


}
