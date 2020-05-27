package rollingcube.results;

import com.google.inject.persist.Transactional;
import util.jpa.GenericJpaDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * DAO class for the {@link GameResult} entity.
 */
public class GameResultDao extends GenericJpaDao<GameResult> {

    /**
     * Constructor for the ResultDao refering to its superclass.
     */
    public GameResultDao() {
        super(GameResult.class);
    }

    /**
     * Returns the list of {@code n} best results with respect to the time
     * spent for solving the puzzle.
     *
     * @param n the maximum number of results to be returned
     * @return the list of {@code n} best results with respect to the time
     * spent for solving the puzzle
     */
    @Transactional
    public List<GameResult> findBest(int n) {
        return entityManager.createQuery("SELECT r FROM GameResult r WHERE r.solved = true ORDER BY r.duration ASC, r.created DESC", GameResult.class)
                .setMaxResults(n)
                .getResultList();
    }

    /**
     * Returns the list of {@code n} best results with respect to the time spent, then with respect to the steps for solving the puzzle.
     *
     * @param n the maximum number of results to be returned.
     * @return the list of {@code n} best results with respect to the time spent for solving the puzzle, then with respect to the steps.
     */
    @Transactional
    public List<GameResult> orderByTime(int n) {
        return entityManager.createQuery("SELECT r FROM GameResult r WHERE r.solved = true ORDER BY r.duration ASC, r.steps ASC", GameResult.class)
                .setMaxResults(n)
                .getResultList();
    }

    /**
     * Returns the list of {@code n} best results with respect to the steps, then with respect to the time spent for solving the puzzle.
     *
     * @param n the maximum number of results to be returned.
     * @return the list of {@code n} best results with respect to the steps, then with respect to the time spent for solving the puzzle.
     */
    @Transactional
    public List<GameResult> orderBySteps(int n) {
        return entityManager.createQuery("SELECT r FROM GameResult r WHERE r.solved = true ORDER BY r.steps ASC, r.duration ASC", GameResult.class)
                .setMaxResults(n)
                .getResultList();
    }
}
