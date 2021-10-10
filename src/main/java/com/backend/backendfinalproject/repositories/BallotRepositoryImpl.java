package com.backend.backendfinalproject.repositories;

import com.backend.backendfinalproject.models.Ballot;
import com.backend.backendfinalproject.models.Product;
import com.backend.backendfinalproject.models.ProductBallot;
import com.backend.backendfinalproject.models.Response;
import com.backend.backendfinalproject.repositories.interfaces.IBallotRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class BallotRepositoryImpl implements IBallotRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ballot> getBallots() {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("FROM Ballot", Ballot.class).getResultList();
    }

    @Override
    public Object getBallot(int id) {
        Session session = entityManager.unwrap(Session.class);
        List<Ballot> ballotList = session.createQuery("FROM Ballot WHERE id = :id", Ballot.class).setParameter("id", id).getResultList();
        return !ballotList.isEmpty() ? ballotList.get(0) : new Response("Ballot Not Found", false);
    }

    @Override
    public Response register(Ballot ballot, List<ProductBallot> products) {
        Ballot newBallot = entityManager.merge(ballot);
        Response response = new Response("Registration Failed", false);

        boolean registerSuccess = true;

        // Por favor crea tu tabla con una query algo asi
        // CREATE TABLE ballots_products(
        //	ballot_id INT NOT NULL,
        //    FOREIGN KEY (ballot_id) REFERENCES ballots(id),
        //    product_id INT NOT NULL,
        //    FOREIGN KEY (product_id) REFERENCES products(id),
        //    quantity INT NOT NULL
        //);
        for (ProductBallot product: products) {
            Product prd = new Product();
            prd.setId(product.getId());

            Query query = entityManager.createNativeQuery("INSERT INTO ballots_products (ballot_id, product_id, quantity) VALUES(?, ?, ?)");
            query.setParameter(1, newBallot);
            query.setParameter(2, prd);
            query.setParameter(3, product.getQuantity());
            int result = query.executeUpdate();

            if (result == 0) {
                registerSuccess = false;
                break;
            }
        }

        if (newBallot != null && registerSuccess) {
            response.setMessage("Registration Success");
            response.setStatus(true);
        }
        return response;
    }
}