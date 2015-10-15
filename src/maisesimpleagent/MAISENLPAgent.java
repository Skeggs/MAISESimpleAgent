/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maisesimpleagent;

import jade.core.AID;
import jade.core.Agent;
//import jade.core.AID;
//import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;

import java.util.ArrayList;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import maise.search.SimpleSearch.*;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


/**
 *
 * @author Administrator
 */
public class MAISENLPAgent extends Agent
{

    /**
     * @param args the command line arguments
     */
     @Override
     protected void setup() 
     {
        // Add the behaviour serving queries from buyer agents
	addBehaviour(new MAISENLPAgent.SearchServer());
 
        DFAgentDescription dfd = new DFAgentDescription();
	dfd.setName(getAID());
        System.out.println("Search Agent " + getAID().getName() + "is ready.");
	ServiceDescription sd = new ServiceDescription();
	sd.setType("MAISESearch");
	sd.setName("MAISESearch");
	dfd.addServices(sd);
        
	try
        {
            DFService.register(this, dfd);
	}
	catch (FIPAException fe)
        {
            fe.printStackTrace();
	}



	// Add the behaviour serving purchase orders from buyer agents
	//addBehaviour(new SearchRequestServer());
         
     }
     
     	// Put agent clean-up operations here
     @Override
     protected void takeDown()
     {
         // Deregister from the yellow pages
         try
         {
             DFService.deregister(this);
	 }
	 catch (FIPAException fe)
         {
             fe.printStackTrace();
		
         }
		// Printout a dismissal message
	System.out.println("Search agent "+getAID().getName()+" terminating.");
     }
     
     private class SearchServer extends CyclicBehaviour
     {
        
        @Override
	public void action()
        {
             System.out.println("Looking for messages for agent named " + myAgent.getName());
	     MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
             System.out.println("Getting message template " + mt.toString());
             ACLMessage msg = myAgent.receive(mt);
 		            
             
             if (msg != null)
             {
                 try
                 {
                     System.out.println("Found message " + msg);
                     //DBConnection conn = (DBConnection) msg.getContentObject();
                     DBSearch dbs = (DBSearch) msg.getContentObject();
                     //DBConnection conn = dbs.getDatabaseConnection();
                     System.out.println("Retrieved message " + dbs);
                     
                     //SimpleSQLSearch sqlSearch = new SimpleSQLSearch();
                     //ArrayList<String> results= sqlSearch.Search(dbs.getDatabaseConnection(), dbs.getSearchString());
                     
                     NLPSQLSearch nlpSearch = new NLPSQLSearch();
                     ArrayList<String> results= nlpSearch.Search(dbs.getDatabaseConnection(), dbs.getSearchString());
                     
                     System.out.println("RESULTS FOUND" + results.size());
                     
                     ACLMessage reply = msg.createReply();
                     if (results != null)
                     {  
                        System.out.println("Sending message back " + results.toString()); 
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(results.toString());
                     }
                     else
                     {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("not-found");
                     }
                     System.out.println("Sending message back to agent " + msg);
                     myAgent.send(reply);
                     System.out.println("Message sent " + reply);
                     
                 }
                 catch(Exception ure)  { ure.printStackTrace(); }               
             }
             else { System.out.println("No messages found."); }

	}
              
    } 
        
}
