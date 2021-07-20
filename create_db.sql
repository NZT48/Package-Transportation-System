
CREATE TABLE Administrator
( 
	Username             varchar(100)  NOT NULL 
)
go

CREATE TABLE City
( 
	CityId               integer  IDENTITY  NOT NULL ,
	Name                 varchar(100)  NOT NULL ,
	PostalCode           varchar(100)  NOT NULL 
)
go

CREATE TABLE Courier
( 
	Profit               decimal(10,3)  NULL ,
	Status               integer  NULL ,
	NumDeliveredPackets  integer  NULL ,
	RegNum               varchar(100)  NULL ,
	Username             varchar(100)  NOT NULL 
)
go

CREATE TABLE CourierRequest
( 
	RegNum               varchar(100)  NOT NULL ,
	Username             varchar(100)  NOT NULL 
)
go

CREATE TABLE District
( 
	DistrictId           integer  IDENTITY  NOT NULL ,
	Name                 varchar(100)  NOT NULL ,
	X                    integer  NOT NULL ,
	Y                    integer  NOT NULL ,
	CityId               integer  NOT NULL 
)
go

CREATE TABLE OfferToTransport
( 
	PacketId             integer  NOT NULL ,
	PricePercent         decimal(10,3)  NULL ,
	Username             varchar(100)  NOT NULL ,
	OfferId              integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE Packet
( 
	PacketId             integer  IDENTITY  NOT NULL ,
	DistrictIdTo         integer  NOT NULL ,
	DistrictIdFrom       integer  NOT NULL ,
	Username             varchar(100)  NOT NULL ,
	Type                 integer  NULL ,
	Weight               decimal(10,3)  NULL ,
	Status               integer  NULL ,
	Cost                 decimal(10,3)  NULL ,
	AcceptanceTime       datetime  NULL 
)
go

CREATE TABLE Transport
( 
	PacketId             integer  NOT NULL ,
	Username             varchar(100)  NOT NULL 
)
go

CREATE TABLE Users
( 
	Name                 varchar(100)  NOT NULL ,
	Surname              varchar(100)  NOT NULL ,
	Username             varchar(100)  NOT NULL ,
	Password             varchar(100)  NOT NULL ,
	NumSentPackets       integer  NULL 
)
go

CREATE TABLE Vehicle
( 
	RegNum               varchar(100)  NOT NULL ,
	FuelType             integer  NOT NULL 
	CONSTRAINT OgranicenjeTipa
		CHECK  ( FuelType BETWEEN 0 AND 2 ),
	Consumption          decimal(10,3)  NOT NULL 
)
go

ALTER TABLE Administrator
	ADD CONSTRAINT XPKAdministrator PRIMARY KEY  CLUSTERED (Username ASC)
go

ALTER TABLE City
	ADD CONSTRAINT XPKCity PRIMARY KEY  CLUSTERED (CityId ASC)
go

ALTER TABLE Courier
	ADD CONSTRAINT XPKCourier PRIMARY KEY  CLUSTERED (Username ASC)
go

ALTER TABLE CourierRequest
	ADD CONSTRAINT XPKCourierRequest PRIMARY KEY  CLUSTERED (RegNum ASC,Username ASC)
go

ALTER TABLE District
	ADD CONSTRAINT XPKDistrict PRIMARY KEY  CLUSTERED (DistrictId ASC)
go

ALTER TABLE OfferToTransport
	ADD CONSTRAINT XPKOfferToTransport PRIMARY KEY  CLUSTERED (OfferId ASC)
go

ALTER TABLE Packet
	ADD CONSTRAINT XPKPacket PRIMARY KEY  CLUSTERED (PacketId ASC)
go

ALTER TABLE Transport
	ADD CONSTRAINT XPKTransport PRIMARY KEY  CLUSTERED (PacketId ASC,Username ASC)
go

ALTER TABLE Users
	ADD CONSTRAINT XPKUsers PRIMARY KEY  CLUSTERED (Username ASC)
go

ALTER TABLE Vehicle
	ADD CONSTRAINT XPKVehicle PRIMARY KEY  CLUSTERED (RegNum ASC)
go


ALTER TABLE Administrator
	ADD CONSTRAINT R_4 FOREIGN KEY (Username) REFERENCES Users(Username)
		ON UPDATE CASCADE
go


ALTER TABLE Courier
	ADD CONSTRAINT R_3 FOREIGN KEY (Username) REFERENCES Users(Username)
		ON UPDATE CASCADE
go

ALTER TABLE Courier
	ADD CONSTRAINT R_19 FOREIGN KEY (RegNum) REFERENCES Vehicle(RegNum)
		ON UPDATE CASCADE
go


ALTER TABLE CourierRequest
	ADD CONSTRAINT R_7 FOREIGN KEY (RegNum) REFERENCES Vehicle(RegNum)
		ON UPDATE CASCADE
go


ALTER TABLE District
	ADD CONSTRAINT R_1 FOREIGN KEY (CityId) REFERENCES City(CityId)
		ON UPDATE CASCADE
go


ALTER TABLE OfferToTransport
	ADD CONSTRAINT R_11 FOREIGN KEY (PacketId) REFERENCES Packet(PacketId)
		ON UPDATE CASCADE
go

ALTER TABLE OfferToTransport
	ADD CONSTRAINT R_18 FOREIGN KEY (Username) REFERENCES Courier(Username)
		ON UPDATE CASCADE
go


ALTER TABLE Packet
	ADD CONSTRAINT R_2 FOREIGN KEY (Username) REFERENCES Users(Username)
		ON UPDATE CASCADE
go

ALTER TABLE Packet
	ADD CONSTRAINT R_12 FOREIGN KEY (DistrictIdFrom) REFERENCES District(DistrictId)
		ON UPDATE CASCADE
go

ALTER TABLE Packet
	ADD CONSTRAINT R_13 FOREIGN KEY (DistrictIdTo) REFERENCES District(DistrictId)
		ON UPDATE CASCADE
go


ALTER TABLE Transport
	ADD CONSTRAINT R_16 FOREIGN KEY (PacketId) REFERENCES Packet(PacketId)
		ON UPDATE CASCADE
go

ALTER TABLE Transport
	ADD CONSTRAINT R_17 FOREIGN KEY (Username) REFERENCES Courier(Username)
		ON UPDATE CASCADE
go
