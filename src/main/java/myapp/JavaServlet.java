package myapp;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Math;

public class JavaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

   // Function to calculate parameters of Basic COCOMO
	private static String calculate(double[][] table, String[] mode, int size) 
	{
        String out = ""; //output String, formatted in JSON
		double effort, time, staff; //stats we are trying to calculate
        int model = 0; //Organic, Semi-detached, Embedded

        int[] drivers = {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3};
        int eaf = 1;
        
        //cost drivers for the intermediate cocomo model, 0.00 is a nonvalue
        double[][] costDrivers = { {0.75, 0.88, 1.00, 1.15, 1.40}, //Required Software Reliability
                                    {0.00, 0.94, 1.00, 1.08, 1.16}, //Size of Application Database
                                    {0.70, 0.85, 1.00, 1.15, 1.30}, //Complexity of The Product
                                    {0.00, 0.00, 1.00, 1.11, 1.30}, //Runtime Performance Constraints
                                    {0.00, 0.00, 1.00, 1.06, 1.21}, //Memory Constraints
                                    {0.00, 0.87, 1.00, 1.15, 1.30}, //Volatility of the virtual machine environment
                                    {0.00, 0.94, 1.00, 1.07, 1.15}, //Required turnabout time
                                    {1.46, 1.19, 1.00, 0.86, 0.71}, //Analyst capability
                                    {1.29, 1.13, 1.00, 0.91, 0.82}, //Applications experience
                                    {1.42, 1.17, 1.00, 0.86, 0.70}, //Software engineer capability
                                    {1.21, 1.10, 1.00, 0.90, 0.00}, //Virtual machine experience
                                    {1.14, 1.07, 1.00, 0.95, 0.00}, //Programming language experience
                                    {1.24, 1.10, 1.00, 0.91, 0.82}, //Application of software engineering methods
                                    {1.24, 1.10, 1.00, 0.91, 0.83}, //Use of software tools
                                    {1.23, 1.08, 1.00, 1.04, 1.10}  //Required development schedule
                                };

		
		// Check the mode according to size 
	    if(size >= 2 && size <= 50)
	    {
	        model = 0;        //organic 
	    }
	    else if(size > 50 && size <= 300) 
	    {
	        model = 1;        //semi-detached 
	    }  
	    else if(size > 300)
	    {
	        model = 2;        //embedded 
	    }
        
        
        //formatting in JSON so we can use a String as a response
	    out += String.format("{ \"size\": \"%d\", \"mode\": \"%s\", ", size, mode[model]);
        
        for (int i = 0; i < drivers.length; ++i)
        {
            eaf *= costDrivers[i][drivers[i]];
        }
        
	    // Calculate Effort 
	    effort = table[model][0]*Math.pow(size,table[model][1]);
	    
	    // Calculate Time 
	    time = table[model][2]*Math.pow(effort,table[model][3]);
	    
	    //Calculate Persons Required 
	    staff = effort/time;
	    
	    // Output the values calculated
	    out += String.format("\"effort\": \"%.3f\", ", effort);
	    out += String.format("\"time\": \"%.5f\", ", time);
        out += String.format("\"staff\": \"%d\" }", fround(staff));

        return out;
	}
	
	// Function for rounding off double to int 
	private static int fround(double x) 
	{
		int a = 0;
		x = x + 0.5;
		a = (int)x;
		return a;
	}

    // handles the JSON request for KLOC size
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // formats the response to be in JSON
        resp.setContentType("application/json");

        double[][] tableBasic = { {2.4, 1.05, 2.5, 0.38}, {3.0, 1.12, 2.5, 0.35}, {3.6, 1.20, 2.5, 0.32} }; //table of values for basic cocomo calculations
        double[][] tableIntermediate = { {3.2, 1.05}, {3.0, 1.12}, {2.8, 1.20} }; //table of values for intermediate cocomo calculations
        String mode[] = {"Organic","Semi-Detached","Embedded"}; //mode
        int size = 0; //KLOC

        String param = req.getParameter("size");
        PrintWriter out = resp.getWriter();
    
        //setting the size to an arbitrary value if none is given
        if (param == null) {
            size = 4;
        }
        else {
            size = Integer.parseInt(param);
        }

        //output the results in JSON format
        out.write(calculate(tableBasic, mode, size));
    }
}