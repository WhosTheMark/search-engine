CREATE TABLE word (
    id_word     varchar(50) NOT NULL,
    CONSTRAINT PK_WORD PRIMARY KEY (id_word)
);

CREATE TABLE document (
    id_document numeric     NOT NULL,
    name        varchar(10) NOT NULL,
    CONSTRAINT PK_DOCUMENT PRIMARY KEY (id_document)
);

-- index is a reserved word of SQL
CREATE TABLE indx (
    id_word     varchar(50) NOT NULL,
    document    numeric     NOT NULL,    
    weight      numeric     NOT NULL,
    CONSTRAINT PK_INDEX PRIMARY KEY (id_word,document),
    CONSTRAINT FK_INDEX_WORD FOREIGN KEY (id_word)
        REFERENCES word (id_word),
    CONSTRAINT FK_INDEX_DOCUMENT FOREIGN KEY (document)
        REFERENCES document (id_document)
);

-- to store calculated tf-idf weights
CREATE TABLE tf_idf_index (
    id_word     varchar(50) NOT NULL,
    document    numeric     NOT NULL,    
    weight      numeric     NOT NULL,
    CONSTRAINT PK_TF_IF_INDEX PRIMARY KEY (id_word,document),
    CONSTRAINT FK_TF_ID_INDEX_INDEX FOREIGN KEY (id_word,document)
        REFERENCES indx (id_word,document)
);

