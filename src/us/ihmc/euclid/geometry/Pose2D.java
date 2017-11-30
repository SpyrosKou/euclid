package us.ihmc.euclid.geometry;

import us.ihmc.euclid.exceptions.NotAMatrix2DException;
import us.ihmc.euclid.geometry.interfaces.Orientation2DBasics;
import us.ihmc.euclid.geometry.interfaces.Pose2DBasics;
import us.ihmc.euclid.geometry.tools.EuclidGeometryIOTools;
import us.ihmc.euclid.interfaces.GeometryObject;
import us.ihmc.euclid.tools.RotationMatrixTools;
import us.ihmc.euclid.transform.interfaces.Transform;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple2D.interfaces.Point2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DReadOnly;

/**
 * A {@code Pose2D} represents a position and orientation in the XY-plane.
 */
public class Pose2D implements Pose2DBasics, GeometryObject<Pose2D>
{
   /** The position part of this pose 2D. */
   private final Point2D position = new Point2D();
   /** The orientation part of this pose 2D. */
   private final Orientation2D orientation = new Orientation2D();
   
   /**
    * Creates a new pose 2D initialized with its position at (0, 0) and orientation at 0.
    */
   public Pose2D()
   {
   }

   /**
    * Creates a new pose 2D and initializes it with the given parameters.
    *
    * @param x the x-coordinate of the position.
    * @param y the y-coordinate of the position.
    * @param yaw the angle in radians for the orientation.
    */
   public Pose2D(double x, double y, double yaw)
   {
      set(x, y, yaw);
   }

   /**
    * Creates a new pose 2D and initializes it to {@code other}.
    *
    * @param other the other pose 2D used to initialize this. Not modified.
    */
   public Pose2D(Pose2D other)
   {
      set(other);
   }

   /**
    * Creates a new pose 2D and initializes it with the given parameters.
    *
    * @param position tuple used to initialize the position part of this pose. Not modified.
    * @param orientation used to initialize the orientation part of this pose. Not modified.
    */
   public Pose2D(Tuple2DReadOnly position, Orientation2D orientation)
   {
      set(position, orientation);
   }

   /** {@inheritDoc} */
   @Override
   public boolean containsNaN()
   {
      return position.containsNaN() || orientation.containsNaN();
   }

   /** {@inheritDoc} */
   @Override
   public void setToNaN()
   {
      position.setToNaN();
      orientation.setToNaN();
   }

   /**
    * Sets the position to (0, 0) and the orientation to zero.
    */
   @Override
   public void setToZero()
   {
      position.setToZero();
      orientation.setToZero();
   }
   
   /** {@inheritDoc} */
   @Override
   public double getX()
   {
      return position.getX();
   }
   
   /** {@inheritDoc} */
   @Override
   public double getY()
   {
      return position.getY();
   }
   
   /** {@inheritDoc} */
   @Override
   public double getYaw()
   {
      return orientation.getYaw();
   }
   
   /** {@inheritDoc} */
   @Override
   public Point2DBasics getPosition()
   {
      return position;
   }

   /** {@inheritDoc} */
   @Override
   public Orientation2DBasics getOrientation()
   {
      return orientation;
   }

   /** {@inheritDoc} */
   @Override
   public void getOrientation(Orientation2D orientationToPack)
   {
      orientationToPack.set(orientation);
   }

   /** {@inheritDoc} */
   @Override
   public void setX(double x)
   {
      position.setX(x);
   }

   /** {@inheritDoc} */
   @Override
   public void setY(double y)
   {
      position.setY(y);
   }

   /** {@inheritDoc} */
   @Override
   public void setYaw(double yaw)
   {
      orientation.setYaw(yaw);
   }

   /**
    * Sets this pose 2D to the {@code other} pose 2D.
    *
    * @param other the other pose 2D. Not modified.
    */
   @Override
   public void set(Pose2D other)
   {
      Pose2DBasics.super.set(other);
   }

   /**
    * Performs a linear interpolation from {@code this} to {@code other} given the percentage
    * {@code alpha}.
    * <p>
    * this.position = (1.0 - alpha) * this.position + alpha * other.position<br>
    * this.orientation = (1.0 - alpha) * this.orientation + alpha * other.orientation
    * </p>
    *
    * @param other the other pose 2D used for the interpolation. Not modified.
    * @param alpha the percentage used for the interpolation. A value of 0 will result in not
    *           modifying {@code this}, while a value of 1 is equivalent to setting {@code this} to
    *           {@code other}.
    */
   public void interpolate(Pose2D other, double alpha)
   {
      position.interpolate(other.position, alpha);
      orientation.interpolate(other.orientation, alpha);
   }

   /**
    * Performs a linear interpolation from {@code pose1} to {@code pose2} given the percentage
    * {@code alpha}.
    * <p>
    * this.position = (1.0 - alpha) * pose1.position + alpha * pose2.position<br>
    * this.orientation = (1.0 - alpha) * pose1.orientation + alpha * pose2.orientation
    * </p>
    *
    * @param pose1 the first pose 2D used in the interpolation. Not modified.
    * @param pose2 the second pose 2D used in the interpolation. Not modified.
    * @param alpha the percentage to use for the interpolation. A value of 0 will result in setting
    *           {@code this} to {@code pose1}, while a value of 1 is equivalent to setting
    *           {@code this} to {@code pose2}.
    */
   public void interpolate(Pose2D pose1, Pose2D pose2, double alpha)
   {
      position.interpolate(pose1.position, pose2.position, alpha);
      orientation.interpolate(pose1.orientation, pose2.orientation, alpha);
   }

   /**
    * Adds the translation (x, y) to this pose 2D assuming it is expressed in the coordinates in
    * which this pose is expressed.
    * <p>
    * If the translation is expressed in the local coordinates described by this pose 2D, use
    * {@link #appendTranslation(double, double)}.
    * </p>
    *
    * @param x the translation distance along the x-axis.
    * @param y the translation distance along the y-axis.
    */
   public void prependTranslation(double x, double y)
   {
      position.add(x, y);
   }

   /**
    * Adds the given {@code translation} to this pose 2D assuming it is expressed in the coordinates
    * in which this pose is expressed.
    * <p>
    * If the {@code translation} is expressed in the local coordinates described by this pose 2D,
    * use {@link #appendTranslation(Tuple2DReadOnly)}.
    * </p>
    *
    * @param translation tuple containing the translation to apply to this pose 2D. Not modified.
    */
   public void prependTranslation(Tuple2DReadOnly translation)
   {
      prependTranslation(translation.getX(), translation.getY());
   }

   /**
    * Rotates the position part of this pose 2D by {@code yaw} and adds {@code yaw} to the
    * orientation part.
    * <p>
    * If the rotation should not affect this pose's position, use {@link #appendRotation(double)}.
    * </p>
    *
    * @param yaw the angle about the z-axis to prepend to this pose 2D.
    */
   public void prependRotation(double yaw)
   {
      RotationMatrixTools.applyYawRotation(yaw, position, position);
      orientation.add(yaw);
   }

   /**
    * Rotates the position part of this pose 2D by {@code orientation} and adds {@code orientation}
    * to the orientation part.
    * <p>
    * If the rotation should not affect this pose's position, use
    * {@link #appendRotation(Orientation2D)}.
    * </p>
    *
    * @param orientation the orientation to prepend to this pose 2D. Not modified.
    */
   public void prependRotation(Orientation2D orientation)
   {
      prependRotation(orientation.getYaw());
   }

   /**
    * Rotates, then adds the translation (x, y) to this pose 2D.
    * <p>
    * Use this method if the translation (x, y) is expressed in the local coordinates described by
    * this pose 2D. Otherwise, use {@link #prependTranslation(double, double)}.
    * </p>
    *
    * @param x the translation distance along the x-axis.
    * @param y the translation distance along the y-axis.
    */
   public void appendTranslation(double x, double y)
   {
      double thisX = position.getX();
      double thisY = position.getY();

      position.set(x, y);
      orientation.transform(position);
      position.add(thisX, thisY);
   }

   /**
    * Rotates, then adds the given {@code translation} to this pose 2D.
    * <p>
    * Use this method if the {@code translation} is expressed in the local coordinates described by
    * this pose 2D. Otherwise, use {@link #prependTranslation(Tuple2DReadOnly)}.
    * </p>
    *
    * @param translation tuple containing the translation to apply to this pose 2D. Not modified.
    */
   public void appendTranslation(Tuple2DReadOnly translation)
   {
      appendTranslation(translation.getX(), translation.getY());
   }

   /**
    * Adds the given {@code yaw} angle to the orientation of this pose 2D.
    * <p>
    * If the position part of this pose 2D is to be rotated by the given angle, use
    * {@link #prependRotation(double)}.
    * </p>
    *
    * @param yaw the angle about the z-axis to append to this pose 2D.
    */
   public void appendRotation(double yaw)
   {
      orientation.add(yaw);
   }

   /**
    * Adds the given {@code orientation} to the orientation of this pose 2D.
    * <p>
    * If the position part of this pose 2D is to be rotated by the given {@code orientation}, use
    * {@link #prependRotation(Orientation2D)}.
    * </p>
    *
    * @param orientation the orientation to append to this pose 2D. Not modified.
    */
   public void appendRotation(Orientation2D orientation)
   {
      appendRotation(orientation.getYaw());
   }

   /**
    * Transforms the position and orientation parts of this pose 2D by the given {@code transform}.
    *
    * @param transform the geometric transform to apply on this pose 2D. Not modified.
    * @throws NotAMatrix2DException if the rotation part of {@code transform} is not a
    *            transformation in the XY plane.
    */
   @Override
   public void applyTransform(Transform transform)
   {
      position.applyTransform(transform);
      orientation.applyTransform(transform);
   }

   /**
    * Transforms the position and orientation parts of this pose 2D by the inverse of the given
    * {@code transform}.
    *
    * @param transform the geometric transform to apply on this pose 2D. Not modified.
    * @throws NotAMatrix2DException if the rotation part of {@code transform} is not a
    *            transformation in the XY plane.
    */
   @Override
   public void applyInverseTransform(Transform transform)
   {
      position.applyInverseTransform(transform);
      orientation.applyInverseTransform(transform);
   }

   /**
    * Tests on a per component basis, if this pose 2D is exactly equal to {@code other}.
    *
    * @param other the other pose 2D to compare against this. Not modified.
    * @return {@code true} if the two poses are exactly equal component-wise, {@code false}
    *         otherwise.
    */
   public boolean equals(Pose2D other)
   {
      if (other == null)
         return false;
      else
         return position.equals(other.position) && orientation.equals(other.orientation);
   }

   /**
    * Tests if the given {@code object}'s class is the same as this, in which case the method
    * returns {@link #equals(Pose2D)}, it returns {@code false} otherwise.
    *
    * @param object the object to compare against this. Not modified.
    * @return {@code true} if {@code object} and this are exactly equal, {@code false} otherwise.
    */
   @Override
   public boolean equals(Object obj)
   {
      try
      {
         return equals((Pose2D) obj);
      }
      catch (ClassCastException e)
      {
         return false;
      }
   }

   /**
    * Tests on a per-component basis if this pose is equal to {@code other} with the tolerance
    * {@code epsilon}.
    *
    * @param other the query. Not modified.
    * @param epsilon the tolerance to use.
    * @return {@code true} if the two poses are equal, {@code false} otherwise.
    */
   @Override
   public boolean epsilonEquals(Pose2D other, double epsilon)
   {
      return Pose2DBasics.super.epsilonEquals(other, epsilon);
   }

   /**
    * Compares {@code this} to {@code other} to determine if the two poses are geometrically
    * similar.
    * <p>
    * Two poses are geometrically equal if both their position and orientation are geometrically
    * equal.
    * </p>
    *
    * @param other the pose to compare to. Not modified.
    * @param epsilon the tolerance of the comparison.
    * @return {@code true} if the two poses represent the same geometry, {@code false} otherwise.
    */
   @Override
   public boolean geometricallyEquals(Pose2D other, double epsilon)
   {
      return Pose2DBasics.super.geometricallyEquals(other, epsilon);
   }

   /**
    * Provides a {@code String} representation of this pose 2D as follows:<br>
    * Pose 2D: position = (x, y), orientation = (yaw)
    *
    * @return the {@code String} representing this pose 2D.
    */
   @Override
   public String toString()
   {
      return EuclidGeometryIOTools.getPose2DString(this);
   }
}
