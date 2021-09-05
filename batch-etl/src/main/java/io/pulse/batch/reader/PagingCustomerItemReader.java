package io.pulse.batch.reader;

import io.pulse.domain.entity.SourceCustomer;
import io.pulse.domain.repositoty.SourceustomerRepository;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.lang.Math.min;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Non-thread safe {@link AbstractItemCountingItemStreamItemReader}.
 *
 * @author Vincent Cornet
 */
public class PagingCustomerItemReader extends AbstractItemCountingItemStreamItemReader<SourceCustomer> {

    private final Logger logger = getLogger(getClass());

    @PersistenceContext
    @Setter
    private EntityManager em;

    @Autowired
    @Setter
    private SourceustomerRepository repository;

    private final int pageSize;
    private int currentItemIndex;
    private List<Long> ids;
    private List<SourceCustomer> currentPage;

    public PagingCustomerItemReader(int pageSize) {
        this.pageSize = pageSize;

        setSaveState(false);
    }

    @Override
    protected SourceCustomer doRead() {
        if (currentItemIndex >= ids.size()) {
            return null; // no more elements
        }

        int indexInPage = currentItemIndex % pageSize;

        // read a new page
        if (indexInPage == 0) {
            em.clear(); // free memory (Hibernate L1 cache)

            int nextPage = min(ids.size(), currentItemIndex + pageSize);
            List<Long> pageIds = ids.subList(currentItemIndex, nextPage);
            currentPage = repository.findAllByIdFetch(pageIds);
        }

        currentItemIndex++;

        // return an item from the page
        return currentPage.get(indexInPage);
    }

    @Override
    protected void doOpen() {
        ids = repository.findAllIds(); // select all customers in a cheap format
        logger.info("Paginating to read {} items", ids.size());
    }

    @Override
    protected void doClose() {
        ids = null;
        currentPage = null;
        currentItemIndex = 0;
    }
}
