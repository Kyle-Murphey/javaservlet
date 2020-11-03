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
        String out = "";
		double effort, time, staff;
		int model = 0;
		
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
        
        //formatting response in JSON so we can use a String
	    out += String.format("{ \"size\": \"%d\", \"mode\": \"%s\", ", size, mode[model]);
	    
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

        double[][] table = { {2.4, 1.05, 2.5, 0.38}, {3.0, 1.12, 2.5, 0.35}, {3.6, 1.20, 2.5, 0.32} }; //table of values for calculations
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
        out.write(calculate(table, mode, size));
    }
}