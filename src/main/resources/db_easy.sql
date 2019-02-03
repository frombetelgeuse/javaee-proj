-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema logistics_easy
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema logistics_easy
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `logistics_easy` DEFAULT CHARACTER SET utf8 ;
USE `logistics_easy` ;

-- -----------------------------------------------------
-- Table `logistics_easy`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logistics_easy`.`cities` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `logistics_easy`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logistics_easy`.`addresses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `city` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_addresses_1`
    FOREIGN KEY (`city`)
    REFERENCES `logistics_easy`.`cities` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_addresses_1_idx` ON `logistics_easy`.`addresses` (`city` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `logistics_easy`.`routes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logistics_easy`.`routes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `logistics_easy`.`cities_routes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logistics_easy`.`cities_routes` (
  `id_city` INT NOT NULL,
  `id_route` INT NOT NULL,
  `num` INT NULL,
  CONSTRAINT `fk_cities_routes_1`
    FOREIGN KEY (`id_city`)
    REFERENCES `logistics_easy`.`cities` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cities_routes_2`
    FOREIGN KEY (`id_route`)
    REFERENCES `logistics_easy`.`routes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_cities_routes_1_idx` ON `logistics_easy`.`cities_routes` (`id_city` ASC) VISIBLE;

-- CREATE INDEX `fk_cities_routes_2_idx` ON `logistics_easy`.`cities_routes` (`id_route` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
