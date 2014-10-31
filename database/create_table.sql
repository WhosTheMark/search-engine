CREATE TABLE word (
    id_word     varchar(50) NOT NULL,
    CONSTRAINT PK_WORD PRIMARY KEY (id_word)
);

-- index is a reserved word of SQL
CREATE TABLE indx (
    id_word     varchar(50) NOT NULL,
    document    numeric     NOT NULL,    
    weight      numeric     NOT NULL,
    CONSTRAINT PK_INDEX PRIMARY KEY (id_word,document),
    CONSTRAINT FK_INDEX_WORD FOREIGN KEY (id_word)
        REFERENCES word (id_word)
);
