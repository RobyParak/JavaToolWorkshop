import csvReader.ReadAnimeFromCSVFile;
import model.Anime;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainRecommendation {
    static List<Anime> animeList = null;
    private static INDArray similarityMatrix;

    public static void main(String[] args) throws FileNotFoundException {
        final ReadAnimeFromCSVFile reader = new ReadAnimeFromCSVFile();
        try {
            // src/main/java/data/anime.csv
            animeList = reader.read("src/main/java/data/anime.csv");

            // Log details of the first 5 anime
            System.out.println("Details of the first 5 anime:");
            for (int i = 0; i < Math.min(5, animeList.size()); i++) {
                Anime anime = animeList.get(i);
                System.out.println("Anime " + anime.name + ":");
                System.out.println("  genre: " + anime.genre);
                System.out.println("  type: " + anime.type);
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        final Anime anime = pickAnime(animeList);
        if (anime == null) {
            return;
        }
        recommendAnime(anime, animeList);

    }

    /**
     * Prompts the user to enter the name of an anime and returns the corresponding Anime object from the list.
     * Keeps asking for an anime until it is found.
     *
     * @param animeList The list of available anime.
     * @return The Anime object corresponding to the entered name.
     */
    private static Anime pickAnime(final List<Anime> animeList) {
        final Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the name of the anime you want to pick:");
            final String animeName = sc.nextLine();
            for (Anime anime : animeList) {
                if (anime.name.equalsIgnoreCase(animeName)) {
                    return anime;
                }
            }
            // Print a message if the anime is not found
            System.out.println("Anime not found. Please try again.");
        }
    }

    /**
     * Recommends similar anime based on the genres of the picked anime.
     * <p>
     * This method converts the genres of the anime list into vectors, calculates the cosine similarity
     * matrix, and recommends similar anime to the picked anime.
     *
     * @param pickedAnime The anime chosen by the user.
     * @param animeList   The list of all available anime.
     */
    private static void recommendAnime(final Anime pickedAnime, final List<Anime> animeList) {
        final INDArray vectors = convertGenresToVectors(animeList);
        similarityMatrix = calculateCosineSimilarity(vectors);

        // Recommend similar anime
        if (pickedAnime != null) {
            List<Anime> recommendations = getListOfRecommendedAnime(pickedAnime, animeList, 5);
            System.out.println("Recommendations for '" + pickedAnime.name + "':");
            for (Anime recommendation : recommendations) {
                System.out.println(recommendation.name);
            }
        }
    }

    /**
     * Converts the genres of the anime list into binary vectors.
     * This method creates a binary vector for each anime, indicating the presence (1) or absence (0) of each genre.
     * These vectors are then used to compute the cosine similarity between anime, which forms the basis for the recommendation system.
     *
     * @param animeList The list of all available anime.
     * @return INDArray containing the binary genre vectors for each anime.
     */
    private static INDArray convertGenresToVectors(final List<Anime> animeList) {
        // This approach converts each anime's genres into a binary vector
        //  indicating the presence (1) or absence (0) of each genre.
        //  These vectors are then used to compute the cosine similarity
        //  between anime, which forms the basis for the recommendation system.
        final List<String> allGenres = extractAllGenres(animeList);
        final int numGenres = allGenres.size();
        final INDArray vectors = Nd4j.create(animeList.size(), numGenres);
        for (int i = 0; i < animeList.size(); i++) {
            final Anime anime = animeList.get(i);
            final String[] animeGenres = anime.genre.split(", ");
            for (String genre : animeGenres) {
                int index = allGenres.indexOf(genre);
                if (index != -1) {
                    vectors.putScalar(i, index, 1);
                }
            }
        }
        return vectors;
    }

    private static List<String> extractAllGenres(final List<Anime> animeList) {
        // Extract all unique genres from the anime list
        List<String> allGenres = new ArrayList<>();
        for (Anime anime : animeList) {
            //this splits the genre string which contains all genres for that anime into an array of genres
            final String[] animeGenres = anime.genre.split(", ");
            for (String genre : animeGenres) {
                if (!allGenres.contains(genre)) {
                    allGenres.add(genre);
                }
            }
        }
        return allGenres;
    }

    /**
     * Calculates the cosine similarity matrix for the given genre vectors.
     * This method computes the cosine similarity between each pair of anime using their binary genre vectors.
     * The cosine similarity is calculated by taking the dot product of the vectors.
     *
     * @param vectors The INDArray containing the binary genre vectors for each anime.
     * @return INDArray containing the cosine similarity matrix.
     */
    private static INDArray calculateCosineSimilarity(final INDArray vectors) {

        return vectors.mmul(vectors.transpose());
    }

    /**
     * Recommends a list of anime similar to the picked anime based on genre similarity.
     *
     * @param pickedAnime        The anime selected by the user for which recommendations are to be found.
     * @param animeList          The complete list of anime to search for recommendations.
     * @param numRecommendations The number of similar anime to recommend.
     * @return A list of recommended anime that are similar to the picked anime.
     */
    private static List<Anime> getListOfRecommendedAnime(final Anime pickedAnime, final List<Anime> animeList, final int numRecommendations) {
        // find the index of the picked anime in the anime list and store it in the variable index

//        if (index == -1) {
//            return new ArrayList<>();
//        }

        // below we need to sort anime by similarity score - starting from getting all similarities in a double array
        // 1. double[] similarities = similarityMatrix.getRow(index).toDoubleVector();
        // 2. make a list of indices and add all the indices of the anime list
        // 3. sort the indices by the similarity score using indices.sort((i1, i2) -> Double.compare(similarities[i2], similarities[i1]));

        // make new list of recommendations and add the anime from the anime list at the index of the sorted indices
        // use the numRecommendations to limit the number of recommendations in the for loop using numRecommendations variable as the limit
        // return the list of recommendations instead of null
        return null;
    }
}




