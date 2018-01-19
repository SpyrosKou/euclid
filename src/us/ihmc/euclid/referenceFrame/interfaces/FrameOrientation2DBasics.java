package us.ihmc.euclid.referenceFrame.interfaces;

import us.ihmc.euclid.geometry.interfaces.Orientation2DReadOnly;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;

public interface FrameOrientation2DBasics extends FixedFrameOrientation2DBasics
{
   /**
    * Sets the reference frame of this orientation 2D without updating or modifying its yaw angle.
    * 
    * @param referenceFrame the new reference frame for this frame orientation 2D.
    */
   void setReferenceFrame(ReferenceFrame referenceFrame);

   /**
    * Sets the yaw angle of this orientation 2D to zero and sets the current reference frame to
    * {@code referenceFrame}.
    * 
    * @param referenceFrame the new reference frame to be associated with this orientation 2D.
    */
   default void setToZero(ReferenceFrame referenceFrame)
   {
      setReferenceFrame(referenceFrame);
      setToZero();
   }

   /**
    * Sets the yaw angle of this orientation 2D to {@link Double#NaN} and sets the current reference
    * frame to {@code referenceFrame}.
    * 
    * @param referenceFrame the new reference frame to be associated with this orientation 2D.
    */
   default void setToNaN(ReferenceFrame referenceFrame)
   {
      setReferenceFrame(referenceFrame);
      setToNaN();
   }

   /**
    * Sets the yaw angle of this orientation 2D and sets its reference frame to
    * {@code referenceFrame}
    * <p>
    * Note that the argument is trimmed to be contained in [-<i>pi</i>, <i>pi</pi>].
    * </p>
    * 
    * @param referenceFrame the new reference frame for this frame orientation 2D.
    * @param yaw the new value for the yaw angle of this orientation 2D.
    */
   default void setIncludingFrame(ReferenceFrame referenceFrame, double yaw)
   {
      setReferenceFrame(referenceFrame);
      setYaw(yaw);
   }

   /**
    * Sets this frame orientation 2D to {@code orientation2DReadOnly} and sets its current frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this frame orientation 2D.
    * @param orientation2DReadOnly the orientation 2D to copy the values from. Not modified.
    */
   default void setIncludingFrame(ReferenceFrame referenceFrame, Orientation2DReadOnly orientation2DReadOnly)
   {
      setReferenceFrame(referenceFrame);
      set(orientation2DReadOnly);
   }

   /**
    * Sets this frame orientation 2D to {@code other}.
    *
    * @param other the other frame orientation 2D to copy the values and reference frame from. Not
    *           modified.
    */
   default void setIncludingFrame(FrameOrientation2DReadOnly other)
   {
      setReferenceFrame(other.getReferenceFrame());
      set((Orientation2DReadOnly) other);
   }
}
