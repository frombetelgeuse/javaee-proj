-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`clients` (
  `idclients` INT NOT NULL AUTO_INCREMENT,
  `name_client` INT NOT NULL,
  PRIMARY KEY (`idclients`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`cities` (
  `idcities` INT NOT NULL AUTO_INCREMENT,
  `name_city` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idcities`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`addresses` (
  `idaddresses` INT NOT NULL AUTO_INCREMENT,
  `name_address` VARCHAR(45) NULL,
  `city_address` INT NOT NULL,
  PRIMARY KEY (`idaddresses`),
  CONSTRAINT `fk_addresses_1`
    FOREIGN KEY (`city_address`)
    REFERENCES `mydb`.`cities` (`idcities`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_addresses_1_idx` ON `mydb`.`addresses` (`city_address` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `mydb`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`orders` (
  `idorder` INT NOT NULL AUTO_INCREMENT,
  `order_status` INT NOT NULL DEFAULT -1,
  `client_order` INT NOT NULL,
  `address_order` INT NOT NULL,
  PRIMARY KEY (`idorder`),
  CONSTRAINT `fk_orders_1`
    FOREIGN KEY (`client_order`)
    REFERENCES `mydb`.`clients` (`idclients`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_orders_2`
    FOREIGN KEY (`address_order`)
    REFERENCES `mydb`.`addresses` (`idaddresses`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_orders_1_idx` ON `mydb`.`orders` (`client_order` ASC) VISIBLE;

-- CREATE INDEX `fk_orders_2_idx` ON `mydb`.`orders` (`address_order` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `mydb`.`stocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`stocks` (
  `idstocks` INT NOT NULL AUTO_INCREMENT,
  `address_stock` INT NOT NULL,
  PRIMARY KEY (`idstocks`),
  CONSTRAINT `fk_stocks_1`
    FOREIGN KEY (`address_stock`)
    REFERENCES `mydb`.`addresses` (`idaddresses`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_stocks_1_idx` ON `mydb`.`stocks` (`address_stock` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `mydb`.`routes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`routes` (
  `idroutes` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idroutes`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`vehicles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`vehicles` (
  `idvehicles` INT NOT NULL AUTO_INCREMENT,
  `city_vehicle` INT NOT NULL,
  `stock_vehicle` INT NULL,
  `route_vehicle` INT NULL,
  `order_vehicle` INT NULL,
  PRIMARY KEY (`idvehicles`),
  CONSTRAINT `fk_vehicles_1`
    FOREIGN KEY (`city_vehicle`)
    REFERENCES `mydb`.`cities` (`idcities`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vehicles_2`
    FOREIGN KEY (`stock_vehicle`)
    REFERENCES `mydb`.`stocks` (`idstocks`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vehicles_3`
    FOREIGN KEY (`route_vehicle`)
    REFERENCES `mydb`.`routes` (`idroutes`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_vehicles_4`
    FOREIGN KEY (`order_vehicle`)
    REFERENCES `mydb`.`orders` (`idorder`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_vehicles_1_idx` ON `mydb`.`vehicles` (`city_vehicle` ASC) VISIBLE;

-- CREATE INDEX `fk_vehicles_2_idx` ON `mydb`.`vehicles` (`stock_vehicle` ASC) VISIBLE;

-- CREATE INDEX `fk_vehicles_3_idx` ON `mydb`.`vehicles` (`route_vehicle` ASC) VISIBLE;

-- CREATE INDEX `fk_vehicles_4_idx` ON `mydb`.`vehicles` (`order_vehicle` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `mydb`.`cargoes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`cargoes` (
  `id_cargoes` INT NOT NULL AUTO_INCREMENT,
  `name_cargo` VARCHAR(45) NOT NULL,
  `quantity_cargo` INT NOT NULL,
  `vehicle_cargo` INT NULL,
  `stock_cargo` INT NULL,
  `order_cargo` INT NULL,
  PRIMARY KEY (`id_cargoes`),
  CONSTRAINT `fk_cargoes_1`
    FOREIGN KEY (`vehicle_cargo`)
    REFERENCES `mydb`.`vehicles` (`idvehicles`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cargoes_2`
    FOREIGN KEY (`stock_cargo`)
    REFERENCES `mydb`.`stocks` (`idstocks`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cargoes_3`
    FOREIGN KEY (`order_cargo`)
    REFERENCES `mydb`.`orders` (`idorder`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_cargoes_1_idx` ON `mydb`.`cargoes` (`vehicle_cargo` ASC) VISIBLE;

-- CREATE INDEX `fk_cargoes_2_idx` ON `mydb`.`cargoes` (`stock_cargo` ASC) VISIBLE;

-- CREATE INDEX `fk_cargoes_3_idx` ON `mydb`.`cargoes` (`order_cargo` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `mydb`.`agencies`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`agencies` (
  `idagencies` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idagencies`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`cities_routes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`cities_routes` (
  `idcities` INT NOT NULL AUTO_INCREMENT,
  `idroutes` INT NOT NULL,
  CONSTRAINT `fk_cities_routes_1`
    FOREIGN KEY (`idcities`)
    REFERENCES `mydb`.`cities` (`idcities`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_cities_routes_2`
    FOREIGN KEY (`idroutes`)
    REFERENCES `mydb`.`routes` (`idroutes`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- CREATE INDEX `fk_cities_routes_1_idx` ON `mydb`.`cities_routes` (`idcities` ASC) VISIBLE;

-- CREATE INDEX `fk_cities_routes_2_idx` ON `mydb`.`cities_routes` (`idroutes` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
