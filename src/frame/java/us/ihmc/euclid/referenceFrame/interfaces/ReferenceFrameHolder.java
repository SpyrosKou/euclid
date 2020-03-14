package us.ihmc.euclid.referenceFrame.interfaces;

import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.referenceFrame.exceptions.ReferenceFrameMismatchException;

/**
 * {@code ReferenceFrameHolder} represents an object to which a {@code ReferenceFrame} can be
 * associated.
 * <p>
 * This interface provides the methods from comparing the reference frames associated with two
 * objects and comparing the reference frame associated to an object against another reference
 * frame.
 * </p>
 */
public interface ReferenceFrameHolder
{
   /**
    * Checks if the frames held by {@code this} and {@code other} match.
    *
    * @param other the other object holding onto the reference frame to compare against the reference
    *              frame held by {@code this}. Not modified.
    * @throws ReferenceFrameMismatchException if the two reference frames are not the same:
    *                                         {@code this.getReferenceFrame() != other.getReferenceFrame()}.
    */
   default void checkReferenceFrameMatch(ReferenceFrameHolder other) throws ReferenceFrameMismatchException
   {
      checkReferenceFrameMatch(other.getReferenceFrame());
   }

   default void checkReferenceFrameMatch(ReferenceFrameHolder otherA, ReferenceFrameHolder otherB) throws ReferenceFrameMismatchException
   {
      checkReferenceFrameMatch(otherA);
      checkReferenceFrameMatch(otherB);
   }

   default void checkReferenceFrameMatch(ReferenceFrameHolder otherA, ReferenceFrameHolder otherB, ReferenceFrameHolder otherC)
         throws ReferenceFrameMismatchException
   {
      checkReferenceFrameMatch(otherA);
      checkReferenceFrameMatch(otherB);
      checkReferenceFrameMatch(otherC);
   }

   default void checkReferenceFrameMatch(ReferenceFrameHolder otherA, ReferenceFrameHolder otherB, ReferenceFrameHolder otherC, ReferenceFrameHolder otherD)
         throws ReferenceFrameMismatchException
   {
      checkReferenceFrameMatch(otherA);
      checkReferenceFrameMatch(otherB);
      checkReferenceFrameMatch(otherC);
      checkReferenceFrameMatch(otherD);
   }

   /**
    * Checks if the frame held by {@code this} matches the query {@code referenceFrame}.
    *
    * @param referenceFrame the query to compare against the reference frame held by {@code this}. Not
    *                       modified.
    * @throws ReferenceFrameMismatchException if the two reference frames are not the same:
    *                                         {@code this.getReferenceFrame() != referenceFrame}.
    */
   default void checkReferenceFrameMatch(ReferenceFrame referenceFrame) throws ReferenceFrameMismatchException
   {
      getReferenceFrame().checkReferenceFrameMatch(referenceFrame);
   }

   /**
    * Gets the reference frame currently associated with {@code this}.
    *
    * @return the reference frame associated with {@code this}.
    */
   public abstract ReferenceFrame getReferenceFrame();
}
