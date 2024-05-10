import csvReader.ReadAnimeFromCSVFile;
import model.Anime;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainRecommendation {
    static List<Anime> animeList = null;
    private static INDArray similarityMatrix;
        public static void main(String[] args) throws FileNotFoundException {
            ReadAnimeFromCSVFile reader = new ReadAnimeFromCSVFile();
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
            Anime anime = pickAnime(animeList);
            recommendAnime(anime, animeList);

        }

    private static Anime pickAnime(List<Anime> animeList) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the anime you want to pick:");
        String animeName = sc.nextLine();
        for (Anime anime : animeList) {
            if (anime.name.equalsIgnoreCase(animeName)) {
                // Print details of the found anime
//                System.out.println("Anime " + anime.name + ":");
//                System.out.println("  genre: " + anime.genre);
//                System.out.println("  type: " + anime.type);
                // Return the found anime
                return anime;
            }
        }
        // Print a message if the anime is not found
        System.out.println("Anime not found.");
        return null;
    }

    private static void recommendAnime(final Anime pickedAnime, final List<Anime> animeList) {
        final INDArray vectors = convertGenresToVectors(animeList);
        similarityMatrix = calculateCosineSimilarity(vectors);

        // Recommend similar anime
        if (pickedAnime != null) {
            List<Anime> recommendations = recommendAnime(pickedAnime, animeList, 5);
            System.out.println("Recommendations for '" + pickedAnime.name + "':");
            for (Anime recommendation : recommendations) {
                System.out.println(recommendation.name);
            }
        }
    }
        private static INDArray convertGenresToVectors(List<Anime> animeList) {
            // Convert anime genres to binary vectors
            List<String> allGenres = extractAllGenres(animeList);
            int numGenres = allGenres.size();
            INDArray vectors = Nd4j.create(animeList.size(), numGenres);
            for (int i = 0; i < animeList.size(); i++) {
                Anime anime = animeList.get(i);
                String[] animeGenres = anime.genre.split(", ");
                for (String genre : animeGenres) {
                    int index = allGenres.indexOf(genre);
                    if (index != -1) {
                        vectors.putScalar(i, index, 1);
                    }
                }
            }
            return vectors;
        }

        private static List<String> extractAllGenres(List<Anime> animeList) {
            // Extract all unique genres from the anime list
            List<String> allGenres = new ArrayList<>();
            for (Anime anime : animeList) {
                String[] animeGenres = anime.genre.split(", ");
                for (String genre : animeGenres) {
                    if (!allGenres.contains(genre)) {
                        allGenres.add(genre);
                    }
                }
            }
            return allGenres;
        }

        private static INDArray calculateCosineSimilarity(INDArray vectors) {
            return vectors.mmul(vectors.transpose());
        }

        private static List<Anime> recommendAnime(Anime pickedAnime, List<Anime> animeList, int numRecommendations) {
            // Find index of picked anime in the list
            int index = animeList.indexOf(pickedAnime);
            if (index == -1) {
                return new ArrayList<>();
            }

            // Sort anime by similarity score
            double[] similarities = similarityMatrix.getRow(index).toDoubleVector();
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < similarities.length; i++) {
                indices.add(i);
            }
            indices.sort((i1, i2) -> Double.compare(similarities[i2], similarities[i1]));

            // Get recommendations
            List<Anime> recommendations = new ArrayList<>();
            for (int i = 0; i < numRecommendations && i < indices.size(); i++) {
                int animeIndex = indices.get(i);
                if (animeIndex != index) {
                    recommendations.add(animeList.get(animeIndex));
                }
            }
            return recommendations;
        }
    }




