import React, { useState } from 'react';

interface ImageWithFallbackProps {
  src?: string | null;
  alt?: string;
  className?: string;
  style?: React.CSSProperties;
  width?: string | number;
  height?: string | number;
}

/**
 * Image component that displays a placeholder when the image fails to load
 * or when no source is provided.
 */
export const ImageWithFallback: React.FC<ImageWithFallbackProps> = ({
  src,
  alt = 'Image',
  className = '',
  style = {},
  width = 'auto',
  height = 'auto',
}) => {
  const [imageError, setImageError] = useState(false);
  const [imageLoading, setImageLoading] = useState(true);

  // Placeholder SVG (gray box with camera icon)
  const placeholderSvg = `data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300' viewBox='0 0 400 300'%3E%3Crect width='400' height='300' fill='%23e9ecef'/%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='Arial, sans-serif' font-size='18' fill='%236c757d'%3ENo Image Available%3C/text%3E%3C/svg%3E`;

  const handleImageError = () => {
    setImageError(true);
    setImageLoading(false);
  };

  const handleImageLoad = () => {
    setImageLoading(false);
    setImageError(false);
  };

  // Show placeholder if no src, empty src, or image failed to load
  const shouldShowPlaceholder = !src || src.trim() === '' || imageError;

  if (shouldShowPlaceholder) {
    return (
      <img
        src={placeholderSvg}
        alt={alt}
        className={`image-placeholder ${className}`}
        style={{
          width,
          height,
          objectFit: 'contain',
          backgroundColor: '#e9ecef',
          border: '1px solid #dee2e6',
          borderRadius: '4px',
          ...style,
        }}
      />
    );
  }

  return (
    <img
      src={src}
      alt={alt}
      className={className}
      style={{
        width,
        height,
        objectFit: 'contain',
        display: imageLoading ? 'none' : 'block',
        ...style,
      }}
      onError={handleImageError}
      onLoad={handleImageLoad}
    />
  );
};

export default ImageWithFallback;
