package de.caritas.cob.statisticsservice.api.statistics.listener;

import de.caritas.cob.statisticsservice.api.statistics.model.statisticsevent.StatisticsEvent;
import lombok.NonNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public abstract class BookingListener {

  protected final @NonNull MongoTemplate mongoTemplate;

  protected BookingListener(@NonNull MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  /**
   * This method is used to keep track of bookings between creation, reschedule and cancellation events.
   * For this, the "currentBookingId" field of all related events is being kept up to date with this method.
   *
   * @param prevBookingId ID of the previous, related event
   * @param newBookingId ID of the new change event
   */
  protected void updateRelatedBookings(Integer prevBookingId, Integer newBookingId) {
    mongoTemplate.updateMulti(
        new Query(new Criteria().orOperator(Criteria.where("metaData.bookingId").is(prevBookingId),
            Criteria.where("metaData.currentBookingId").is(prevBookingId))),
        new Update().set("metaData.currentBookingId", newBookingId),
        StatisticsEvent.class
    );
  }
}
