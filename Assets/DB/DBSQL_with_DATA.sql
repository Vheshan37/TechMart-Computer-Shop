-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.29 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for tech_mart
CREATE DATABASE IF NOT EXISTS `tech_mart` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tech_mart`;

-- Dumping structure for table tech_mart.address
CREATE TABLE IF NOT EXISTS `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line1` varchar(45) NOT NULL,
  `line2` varchar(45) NOT NULL,
  `postal_code` varchar(10) NOT NULL,
  `city_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_address_city1_idx` (`city_id`),
  KEY `fk_address_user1_idx` (`user_id`),
  CONSTRAINT `fk_address_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`),
  CONSTRAINT `fk_address_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.address: ~0 rows (approximately)

-- Dumping structure for table tech_mart.brand
CREATE TABLE IF NOT EXISTS `brand` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.brand: ~45 rows (approximately)
REPLACE INTO `brand` (`id`, `brand`) VALUES
	(1, 'Apple'),
	(2, 'Dell'),
	(3, 'ASUS'),
	(4, 'HP'),
	(5, 'Lenovo'),
	(6, 'Microsoft'),
	(7, 'Acer'),
	(8, 'MSI'),
	(9, 'Razer'),
	(10, 'Samsung'),
	(11, 'Corsair'),
	(12, 'CyberPowerPC'),
	(13, 'NZXT'),
	(14, 'Intel'),
	(15, 'CyberPowerPC'),
	(16, 'NVIDIA'),
	(17, 'Gigabyte'),
	(18, 'Sennheiser'),
	(19, 'Audio-Technica'),
	(20, 'JBL'),
	(21, 'SteelSeries'),
	(22, 'Beyerdynamic'),
	(23, 'Audio-Technica'),
	(24, 'Kingston'),
	(25, 'Patriot'),
	(26, 'Corsair'),
	(27, 'TeamGroup'),
	(28, 'Crucial'),
	(29, 'Samsung'),
	(30, 'Seagate'),
	(31, 'Western Digital'),
	(32, 'ADATA'),
	(33, 'Toshiba'),
	(34, 'Belkin'),
	(35, 'Aukey'),
	(36, 'UGREEN'),
	(37, 'Monoprice'),
	(38, 'Anker'),
	(39, 'Logitech'),
	(40, 'ViewSonic'),
	(41, 'BenQ'),
	(42, 'Roccat'),
	(43, 'Cooler Master'),
	(44, 'Zowie'),
	(45, 'Glorious');

-- Dumping structure for table tech_mart.cart
CREATE TABLE IF NOT EXISTS `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `user_id` int NOT NULL,
  `qty` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cart_item_product1_idx` (`product_id`),
  KEY `fk_cart_item_user1_idx` (`user_id`),
  CONSTRAINT `fk_cart_item_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_cart_item_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.cart: ~0 rows (approximately)

-- Dumping structure for table tech_mart.category
CREATE TABLE IF NOT EXISTS `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.category: ~2 rows (approximately)
REPLACE INTO `category` (`id`, `category`) VALUES
	(1, 'Laptop'),
	(2, 'Desktop PC'),
	(3, 'Monitor'),
	(4, 'Keyboard'),
	(5, 'Mouse'),
	(6, 'Gaming Chair'),
	(7, 'Headphone'),
	(8, 'Motherboard'),
	(9, 'VGA'),
	(10, 'PSU (Power Supply Unit)'),
	(11, 'Mouse Pad'),
	(12, 'RAM'),
	(13, 'Storage (HDD/SSD)'),
	(14, 'Cables');

-- Dumping structure for table tech_mart.category_has_brand
CREATE TABLE IF NOT EXISTS `category_has_brand` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `brand_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category_has_brand_brand1_idx` (`brand_id`),
  KEY `fk_category_has_brand_category1_idx` (`category_id`),
  CONSTRAINT `fk_category_has_brand_brand1` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `fk_category_has_brand_category1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.category_has_brand: ~130 rows (approximately)
REPLACE INTO `category_has_brand` (`id`, `category_id`, `brand_id`) VALUES
	(1, 1, 1),
	(2, 1, 2),
	(3, 1, 3),
	(4, 1, 4),
	(5, 1, 5),
	(6, 1, 6),
	(7, 1, 7),
	(8, 1, 8),
	(9, 1, 9),
	(10, 1, 10),
	(11, 2, 1),
	(12, 2, 2),
	(13, 2, 3),
	(14, 2, 4),
	(15, 2, 5),
	(16, 2, 6),
	(17, 2, 7),
	(18, 2, 8),
	(19, 2, 9),
	(20, 2, 10),
	(21, 3, 10),
	(22, 3, 39),
	(23, 3, 40),
	(24, 3, 41),
	(25, 3, 42),
	(26, 3, 43),
	(27, 3, 44),
	(28, 3, 45),
	(29, 3, 39),
	(30, 3, 17),
	(31, 4, 11),
	(32, 4, 12),
	(33, 4, 13),
	(34, 4, 14),
	(35, 4, 15),
	(36, 4, 16),
	(37, 4, 17),
	(38, 4, 18),
	(39, 4, 19),
	(40, 4, 20),
	(41, 5, 21),
	(42, 5, 22),
	(43, 5, 23),
	(44, 5, 24),
	(45, 5, 25),
	(46, 5, 26),
	(47, 5, 27),
	(48, 5, 28),
	(49, 5, 29),
	(50, 5, 30),
	(51, 6, 31),
	(52, 6, 32),
	(53, 6, 33),
	(54, 6, 34),
	(55, 6, 35),
	(56, 6, 36),
	(57, 6, 37),
	(58, 6, 38),
	(59, 6, 39),
	(60, 6, 40),
	(61, 7, 39),
	(62, 7, 40),
	(63, 7, 41),
	(64, 7, 42),
	(65, 7, 43),
	(66, 7, 44),
	(67, 7, 45),
	(68, 7, 39),
	(69, 7, 18),
	(70, 7, 19),
	(71, 8, 41),
	(72, 8, 42),
	(73, 8, 43),
	(74, 8, 44),
	(75, 8, 45),
	(76, 8, 39),
	(77, 8, 18),
	(78, 8, 17),
	(79, 8, 16),
	(80, 8, 14),
	(81, 9, 16),
	(82, 9, 17),
	(83, 9, 18),
	(84, 9, 19),
	(85, 9, 20),
	(86, 9, 21),
	(87, 9, 22),
	(88, 9, 23),
	(89, 9, 24),
	(90, 9, 25),
	(91, 11, 21),
	(92, 11, 22),
	(93, 11, 23),
	(94, 11, 24),
	(95, 11, 25),
	(96, 11, 26),
	(97, 11, 27),
	(98, 11, 28),
	(99, 11, 29),
	(100, 11, 30),
	(101, 12, 11),
	(102, 12, 12),
	(103, 12, 13),
	(104, 12, 14),
	(105, 12, 15),
	(106, 12, 16),
	(107, 12, 17),
	(108, 12, 18),
	(109, 12, 19),
	(110, 12, 20),
	(111, 13, 30),
	(112, 13, 31),
	(113, 13, 32),
	(114, 13, 33),
	(115, 13, 34),
	(116, 13, 35),
	(117, 13, 36),
	(118, 13, 37),
	(119, 13, 38),
	(120, 13, 39),
	(121, 14, 40),
	(122, 14, 41),
	(123, 14, 42),
	(124, 14, 43),
	(125, 14, 44),
	(126, 14, 45),
	(127, 14, 39),
	(128, 14, 18),
	(129, 14, 19),
	(130, 14, 20);

-- Dumping structure for table tech_mart.city
CREATE TABLE IF NOT EXISTS `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `district_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_city_district1_idx` (`district_id`),
  CONSTRAINT `fk_city_district1` FOREIGN KEY (`district_id`) REFERENCES `district` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.city: ~0 rows (approximately)

-- Dumping structure for table tech_mart.color
CREATE TABLE IF NOT EXISTS `color` (
  `id` int NOT NULL AUTO_INCREMENT,
  `color` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.color: ~2 rows (approximately)

-- Dumping structure for table tech_mart.district
CREATE TABLE IF NOT EXISTS `district` (
  `id` int NOT NULL AUTO_INCREMENT,
  `district` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.district: ~0 rows (approximately)

-- Dumping structure for table tech_mart.feature
CREATE TABLE IF NOT EXISTS `feature` (
  `id` int NOT NULL AUTO_INCREMENT,
  `feature` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.feature: ~2 rows (approximately)
REPLACE INTO `feature` (`id`, `feature`) VALUES
	(1, 'Brand'),
	(2, 'Model Name/Number'),
	(3, 'Price'),
	(4, 'Stock'),
	(5, 'Warranty Period'),
	(6, 'Material'),
	(7, 'Weight'),
	(8, 'OS'),
	(9, 'Processor'),
	(10, 'RAM'),
	(11, 'Storage'),
	(12, 'Graphics Card'),
	(13, 'Screen Size'),
	(14, 'Battery Life'),
	(15, 'Motherboard'),
	(16, 'Adjustable Armrests'),
	(17, 'Reclining Angle'),
	(18, 'Weight Capacity'),
	(19, 'RGB Lighting'),
	(20, 'Type (Over-Ear, In-Ear)'),
	(21, 'Connectivity (Wired, Wireless)'),
	(22, 'Noise Cancellation'),
	(23, 'Driver Size'),
	(24, 'Microphone'),
	(25, 'Memory Size'),
	(26, 'Memory Type'),
	(27, 'Ports'),
	(28, 'Base Clock'),
	(29, 'Boost Clock'),
	(30, 'Cores'),
	(31, 'Threads'),
	(32, 'Clock Speed'),
	(33, 'Architecture'),
	(34, 'TDP (Thermal Design Power)'),
	(35, 'Max Speed'),
	(36, 'Range'),
	(37, 'Dual Band'),
	(38, 'Refresh Rate'),
	(39, 'Resolution'),
	(40, 'Panel Type'),
	(41, 'Switch Type (Mechanical, Membrane)'),
	(42, 'Layout (Full-size, TKL, 60%)'),
	(43, 'Backlight (RGB, Single Color, None)'),
	(44, 'Key Rollover (6-Key, N-Key)'),
	(45, 'Multimedia Keys'),
	(46, 'Macro Keys'),
	(47, 'DPI (Dots Per Inch)'),
	(48, 'Sensor Type (Optical, Laser)'),
	(49, 'Buttons (Number of Buttons)'),
	(50, 'Adjustable Weight'),
	(51, 'Cable Type (HDMI, USB, Ethernet, DisplayPort)'),
	(52, 'Length (1m, 2m, etc.)'),
	(53, 'Connector Type (Male-Male, Male-Female)'),
	(54, 'Bandwidth/Speed (e.g., 10Gbps)'),
	(55, 'Wattage (500W, 750W, etc.)'),
	(56, 'Efficiency Rating (80+ Bronze, Gold, Platinum)'),
	(57, 'Modularity (Non-Modular, Semi-Modular, Fully Modular)'),
	(58, 'Form Factor (ATX, SFX)'),
	(59, 'Screen Size (in inches)'),
	(60, 'Response Time (ms)'),
	(61, 'Aspect Ratio (16:9, 21:9)'),
	(62, 'Adjustable Stand (Height, Tilt, Swivel)'),
	(63, 'Storage Capacity'),
	(64, 'Display Type (Touchscreen, Anti-Glare)'),
	(65, 'Frequency Response'),
	(66, 'Size (Small, Medium, Large, Extended)'),
	(67, 'Surface Type (Smooth, Textured)'),
	(68, 'Wrist Rest (Yes/No)'),
	(69, 'Socket Type (LGA1700, AM5)'),
	(70, 'Chipset (B450, Z790)'),
	(71, 'Supported RAM Type (DDR4, DDR5)'),
	(72, 'PCIe Slots (Gen 3, Gen 4, etc.)'),
	(73, 'Storage Interfaces (SATA, M.2)'),
	(74, 'Rear I/O Ports (USB, HDMI, Ethernet)'),
	(75, 'GPU Model (e.g., NVIDIA RTX 3060)'),
	(76, 'Memory Size (e.g., 6GB, 12GB)'),
	(77, 'Boost Clock Speed'),
	(78, 'Controllers Included');

-- Dumping structure for table tech_mart.feature_list
CREATE TABLE IF NOT EXISTS `feature_list` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `feature_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category_has_feature_feature1_idx` (`feature_id`),
  KEY `fk_category_has_feature_category1_idx` (`category_id`),
  CONSTRAINT `fk_category_has_feature_category1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_category_has_feature_feature1` FOREIGN KEY (`feature_id`) REFERENCES `feature` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.feature_list: ~2 rows (approximately)
REPLACE INTO `feature_list` (`id`, `category_id`, `feature_id`) VALUES
	(1, 1, 9),
	(2, 1, 10),
	(3, 1, 11),
	(4, 1, 12),
	(5, 1, 14),
	(6, 1, 64),
	(7, 1, 8),
	(8, 1, 7),
	(9, 1, 6),
	(10, 2, 12),
	(11, 2, 15),
	(12, 1, 15),
	(13, 2, 8),
	(14, 2, 27),
	(15, 2, 9),
	(16, 2, 10),
	(17, 2, 74),
	(18, 2, 11),
	(19, 2, 73),
	(20, 2, 71),
	(21, 2, 7),
	(22, 2, 55);

-- Dumping structure for table tech_mart.order
CREATE TABLE IF NOT EXISTS `order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_time` datetime NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_user1_idx` (`user_id`),
  CONSTRAINT `fk_order_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.order: ~0 rows (approximately)

-- Dumping structure for table tech_mart.order_item
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_item_order1_idx` (`order_id`),
  KEY `fk_order_item_product1_idx` (`product_id`),
  CONSTRAINT `fk_order_item_order1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  CONSTRAINT `fk_order_item_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.order_item: ~0 rows (approximately)

-- Dumping structure for table tech_mart.product
CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `price` double NOT NULL DEFAULT '0',
  `qty` int NOT NULL DEFAULT '0',
  `delivery_in` double NOT NULL DEFAULT '0',
  `delivery_out` double NOT NULL DEFAULT '0',
  `date_time` datetime NOT NULL,
  `color_id` int NOT NULL,
  `category_has_brand_id` int NOT NULL,
  `district_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_color1_idx` (`color_id`),
  KEY `fk_product_category_has_brand1_idx` (`category_has_brand_id`),
  KEY `fk_product_district1_idx` (`district_id`),
  KEY `fk_product_user1_idx` (`user_id`),
  CONSTRAINT `fk_product_category_has_brand1` FOREIGN KEY (`category_has_brand_id`) REFERENCES `category_has_brand` (`id`),
  CONSTRAINT `fk_product_color1` FOREIGN KEY (`color_id`) REFERENCES `color` (`id`),
  CONSTRAINT `fk_product_district1` FOREIGN KEY (`district_id`) REFERENCES `district` (`id`),
  CONSTRAINT `fk_product_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.product: ~0 rows (approximately)

-- Dumping structure for table tech_mart.product_has_feature_list
CREATE TABLE IF NOT EXISTS `product_has_feature_list` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` text NOT NULL,
  `feature_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_has_feature_list_product1_idx` (`product_id`),
  KEY `fk_product_has_feature_list_feature1_idx` (`feature_id`),
  CONSTRAINT `fk_product_has_feature_list_feature1` FOREIGN KEY (`feature_id`) REFERENCES `feature` (`id`),
  CONSTRAINT `fk_product_has_feature_list_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.product_has_feature_list: ~0 rows (approximately)

-- Dumping structure for table tech_mart.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(20) NOT NULL,
  `verification` varchar(10) NOT NULL DEFAULT '111111',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table tech_mart.user: ~1 rows (approximately)
REPLACE INTO `user` (`id`, `first_name`, `last_name`, `mobile`, `email`, `password`, `verification`) VALUES
	(2, 'Vihanga', 'Heshan', '0719892932', 'vihangaheshan37@gmail.com', 'Vh2002@#', 'verified');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
