import java.util.*;
import java.io.*;

public class MaxCapacityAirspace {

	int V;

	MaxCapacityAirspace(int v) {
		this.V = v;
	}

	int fordFulkerson(int flightgraph[][], int source, int sink) {
		int u, v;

		int adjMatrixGraph[][] = new int[V][V];

		for (u = 0; u < V; u++)
			for (v = 0; v < V; v++)
				adjMatrixGraph[u][v] = flightgraph[u][v];

		int parent[] = new int[V];

		int maxFlow = 0;

		// augmenting a flow while there is a path from source to sink
		while (breadthFirstSearch(adjMatrixGraph, source, sink, parent)) {

			int flowInPath = Integer.MAX_VALUE;
			for (v = sink; v != source; v = parent[v]) {
				u = parent[v];
				flowInPath = Math.min(flowInPath, adjMatrixGraph[u][v]);
			}

			for (v = sink; v != source; v = parent[v]) {
				u = parent[v];
				adjMatrixGraph[u][v] -= flowInPath;
				adjMatrixGraph[v][u] += flowInPath;
			}

			maxFlow += flowInPath;
		}

		return maxFlow;
	}

	// This is basically to see whether there exists a path from source to sink in a
	// residual graph
	boolean breadthFirstSearch(int adjMatrixGraph[][], int source, int sink, int parent[]) {

		boolean visited[] = new boolean[V];
		for (int i = 0; i < V; ++i)
			visited[i] = false;

		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(source);
		visited[source] = true;
		parent[sink] = -1;

		while (queue.size() != 0) {
			int u = queue.poll();

			for (int v = 0; v < V; v++) {
				if (visited[v] == false && adjMatrixGraph[u][v] > 0) {
					queue.add(v);
					parent[v] = u;
					visited[v] = true;
				}
			}
		}

		return (visited[sink] == true);
	}

	private static List<FlightCityDetail> read_input_csv() {

		// Read Data from CSV
		BufferedReader br = null;

		// Please input your data file here
		String inputDetailsFile = "C:/My Stuff- DISK F/ASU Sem 1/FOA/HW4/dataset.csv";
		String line = "";

		// Storing the data so that it can be converted to an adjacency matrix
		List<FlightCityDetail> total_data = new ArrayList<FlightCityDetail>();

		try {
			br = new BufferedReader(new FileReader(inputDetailsFile));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] totalFlightDetail = line.split(",");

				FlightCityDetail fcd = new FlightCityDetail();
				fcd.airlines_carrier = totalFlightDetail[0];
				fcd.departing_city = totalFlightDetail[1];
				fcd.departure_time = totalFlightDetail[2];
				fcd.departure_time_formatted = totalFlightDetail[3];
				fcd.arrival_time = totalFlightDetail[4];
				fcd.arrival_time_formatted = totalFlightDetail[5];
				fcd.arrival_city = totalFlightDetail[6];
				fcd.carrier_capacity = totalFlightDetail[7];
				fcd.capacity = totalFlightDetail[8];
				fcd.date_of_departure = totalFlightDetail[9];
				fcd.date_of_arrival = totalFlightDetail[10];

				total_data.add(fcd);
			}

		} catch (FileNotFoundException e) { // File Not Found Exception
			e.printStackTrace();
		} catch (IOException e) { // Any other exception encountered
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return total_data;
	}

	public static void main(String args[]) {

		int[][] adjMatrixGraph = new int[242][242];
		List<FlightCityDetail> totalFlightDetails = new ArrayList<FlightCityDetail>();

		// get complete data from csv file into Object totalFlightDetails
		totalFlightDetails = read_input_csv();

		// Eased implementation for filling the adjacency matrix
		HashMap<String, Integer> hmapCity = new HashMap<String, Integer>();
		hmapCity.put("LAX", 1);
		hmapCity.put("SFO", 2);
		hmapCity.put("PHX", 3);
		hmapCity.put("SEA", 4);
		hmapCity.put("DEN", 5);
		hmapCity.put("ATL", 6);
		hmapCity.put("ORD", 7);
		hmapCity.put("BOS", 8);
		hmapCity.put("IAD", 9);
		hmapCity.put("JFK", 10);

		/*
		 * Forming the adjacency matrix which can be provided as a simpler
		 * representation of graph for finding the maximum capacity
		 */
		for (FlightCityDetail tfd : totalFlightDetails) {

			// One can change the date in the following line to find the maximum NAS
			// capacity for a particular date
			if (tfd.getDate_of_departure().equals("06-Jan-20") && tfd.getDate_of_arrival().equals("06-Jan-20")) {
				int depCity = hmapCity.get(tfd.getDeparting_city());
				int depTime = Integer.parseInt(tfd.getDeparture_time_formatted());
				int arrCity = hmapCity.get(tfd.getArrival_city());
				int arrTime = Integer.parseInt(tfd.getArrival_time_formatted());
				adjMatrixGraph[(depCity - 1) * 24 + (depTime + 1)][(arrCity - 1) * 24 + (arrTime + 1)] += Integer
						.parseInt(tfd.getCapacity());
			}
		}

		// Considering the possibility of passenger waiting at an airport and then
		// boarding an another flight for reaching the destination
		for (int city = 1; city <= 10; city++) {
			for (int time = 0; time < 23; time++) {
				adjMatrixGraph[(city - 1) * 24 + (time + 1)][(city - 1) * 24 + (time + 2)] = Integer.MAX_VALUE;
			}
		}

		/*
		 * Since we have considered the representation of each city as 24 nodes we have
		 * added a source and a sink node with maximum weights for edges going from
		 * source to LAX and maximum weight edges going from JFK to sink
		 */

		int source = 0;
		int sink = 241;
		
		// Maximum weight edges going from source node to LAX
		for (int i = 1; i < 25; i++) {
			adjMatrixGraph[source][i] = Integer.MAX_VALUE;
		}
		// Maximum weight edges going from JFK to sink
		for (int i = 217; i < 241; i++) {
			adjMatrixGraph[i][sink] = Integer.MAX_VALUE;
		}

		MaxCapacityAirspace m = new MaxCapacityAirspace(adjMatrixGraph.length);

		System.out.println("The capacity of NAS between LAX and JFK for Date: 6th Jan 2020 is "
				+ m.fordFulkerson(adjMatrixGraph, 0, adjMatrixGraph.length - 1));

	}
}
