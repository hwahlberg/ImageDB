-- Table: TAGS

DROP TABLE IF EXISTS TAGS CASCADE;

CREATE TABLE TAGS
(
  TAG_ID	integer,
  TAG_TEXT 	character varying(20),
  CONSTRAINT TAGID_PK PRIMARY KEY (TAG_ID)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE TAGS
  OWNER TO photodb;



-- Table: IMAGE

DROP TABLE IF EXISTS IMAGE CASCADE;

CREATE TABLE IMAGE
(
  IMAGE_ID	bigint NOT NULL,
  FILENAME 	character varying(50) NOT NULL,
  DIRECTORY 	character varying(128) NOT NULL,
  FILESIZE 	character varying(20),
  FILETYPE 	character varying(20),
  IMAGEWIDTH 	integer,
  IMAGEHEIGHT 	integer,
  CONSTRAINT IMAGEID_PK PRIMARY KEY (IMAGE_ID),
  CONSTRAINT "FILENAME_UNQ" UNIQUE (FILENAME)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE IMAGE
  OWNER TO photodb;



-- Table: TAGS_XREF

DROP TABLE IF EXISTS TAGS_XREF CASCADE;

CREATE TABLE TAGS_XREF
(
  TAG_ID        integer,
  IMAGE_ID      bigint,
  CONSTRAINT TAGID_FK FOREIGN KEY (TAG_ID)
      REFERENCES TAGS (TAG_ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT IMAGEID_FK FOREIGN KEY (IMAGE_ID)
      REFERENCES IMAGE (IMAGE_ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE TAGS_XREF
  OWNER TO photodb;






-- Table: COMPOSITE

DROP TABLE IF EXISTS COMPOSITE CASCADE;

CREATE TABLE COMPOSITE
(
  IMAGE_ID bigint,
  APARTURE numeric,
  CONSTRAINT IMAGEID_FK FOREIGN KEY (IMAGE_ID)
      REFERENCES IMAGE (IMAGE_ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE COMPOSITE
  OWNER TO photodb;





-- Table: CAMERA

DROP TABLE IF EXISTS CAMERA CASCADE;

CREATE TABLE CAMERA
(
  CAMERA_ID		  bigint NOT NULL,
  IMAGE_ID		  bigint NOT NULL,
  APERTURE		  character varying(50),       
  CAMERAMODELNAME 	  character varying(50),
  COLORMODE 		  character varying(50),
  COLORSPACE 		  character varying(50),
  COLORTEMPERATURE 	  character varying(50),
  COLORTEMPERATURESETTING character varying(50),
  CREATEDATE 		  character varying(50),
  EXIFIMAGEHEIGHT 	  character varying(50),
  EXIFIMAGEWIDTH 	  character varying(50),
  EXPOSUREMODE 		  character varying(50),
  EXPOSUREPROGRAM 	  character varying(50),
  EXPOSURETIME 		  character varying(50),
  FNUMBER 		  character varying(50),
  FILESOURCE 		  character varying(50),
  FLASH 		  character varying(50),
  FOCALLENGTH 		  character varying(50),
  FOCUSDISTANCE 	  character varying(50),
  FOCUSMODE 		  character varying(50),
  FOCUSPOSITION 	  character varying(50),
  ISO 			  character varying(50),
  LENSTYPE 		  character varying(50),
  MAKE 			  character varying(50),
  METERINGMODE 		  character varying(50),
  MODIFYDATE 		  character varying(50),
  ORIENTATION 		  character varying(50),
  QUALITY 		  character varying(50),
  SHUTTERSPEED 		  character varying(50),
  WHITEBALANCE 		  character varying(50),
  CONSTRAINT CAMERAID_PK PRIMARY KEY (CAMERA_ID),
  CONSTRAINT IMAGEID_FK FOREIGN KEY (IMAGE_ID)
      REFERENCES IMAGE (IMAGE_ID) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE COMPOSITE
  OWNER TO photodb;


