package org.opensourcephysics.stp;

import org.opensourcephysics.controls.*;
import java.io.BufferedInputStream;
import java.util.Vector;


public class CmdLineTestApp extends AbstractCalculation{
   public void reset(){
     control.setValue("arg 0","java");
     control.setValue("arg 1","-classpath");
     control.setValue("arg 2","osp_stp.jar");
     control.setValue("arg 3","org.opensourcephysics.stp.approach.LJgasApp");
     control.setValue("arg 4","ljgas_data.xml");
   }

   public void calculate(){
      final Vector cmd = new Vector();
      String arg=control.getString("arg 0").trim();
      if(arg.length()>0)cmd.add(arg);
      arg=control.getString("arg 1").trim();
      if(arg.length()>0)cmd.add(arg);
      arg=control.getString("arg 2").trim();
      if(arg.length()>0)cmd.add(arg);
      arg=control.getString("arg 3").trim();
      if(arg.length()>0)cmd.add(arg);
      arg=control.getString("arg 4").trim();
      if(arg.length()>0)cmd.add(arg);
      OSPLog.fine(cmd.toString());
      String[] cmdarray = (String[]) cmd.toArray(new String[0]);
      try {
         Process proc = Runtime.getRuntime().exec(cmdarray);
         BufferedInputStream errStream=new BufferedInputStream(proc.getErrorStream());
         StringBuffer buff= new StringBuffer();
         while(true){
           int datum=errStream.read();
           if(datum==-1) break;
           buff.append((char)datum);
         }
         errStream.close();
         String msg=buff.toString().trim();
         if(msg.length()>0){
           OSPLog.info("error buffer: " + buff.toString());
         }
      } catch(Exception ex) {
         OSPLog.info(ex.toString());
      }

   }

   public static void main(String[] args){
      CalculationControl.createApp(new CmdLineTestApp(), args).addControlListener("changed");
   }


}
