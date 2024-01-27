package edu.escuelaing.arem.ASE.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private HttpConnectionExample apiConnectionToTry;
    /**
     * Create the test case
     *
     * @param httpConnectionExample name of the test case
     */
    public AppTest( String httpConnectionExample )
    {
        super( httpConnectionExample );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    protected void setUp() {
        apiConnectionToTry = new HttpConnectionExample();
    }

    /**
     * Rigourous Test :-)
     */
    public void testNonExistentMovie()
    {
        StringBuffer wrongRequest;
        try {
            wrongRequest = apiConnectionToTry.getMovieInfo("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("{\"Response\":\"False\",\"Error\":\"Incorrect IMDb ID.\"}", wrongRequest.toString());
    }

    public void testExistentMovie()
    {
        StringBuffer correctRequest;
        try {
            correctRequest = apiConnectionToTry.getMovieInfo("Betty la fea");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals("{\"Title\":\"Yo soy Betty, la fea\",\"Year\":\"1999–2001\",\"Rated\":\"TV-PG\",\"Released\":\"25 Oct 1999\",\"Runtime\":\"30 min\",\"Genre\":\"Comedy, Drama, Romance\",\"Director\":\"N/A\",\"Writer\":\"N/A\",\"Actors\":\"Ana María Orozco, Jorge Enrique Abello, Natalia Ramírez\",\"Plot\":\"An outcast in a prominent fashion company, a sweet-hearted and unattractive assistant falls hopelessly in love with her boss.\",\"Language\":\"Spanish\",\"Country\":\"Colombia\",\"Awards\":\"5 wins\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BM2U4YzM5ZTItNGFmZi00MDRhLTgyMjUtZDJjMTI2ZmU5ZjNlXkEyXkFqcGdeQXVyMTcxNTYyMjM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.3/10\"}],\"Metascore\":\"N/A\",\"imdbRating\":\"8.3\",\"imdbVotes\":\"3,775\",\"imdbID\":\"tt0233127\",\"Type\":\"series\",\"totalSeasons\":\"1\",\"Response\":\"True\"}", correctRequest.toString());
    }


}
