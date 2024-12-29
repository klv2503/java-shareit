INSERT INTO users (name, email)
          VALUES
              ('First user', 'first@email.com'),
              ('Second user', 'second@email.com'),
              ('Third user', 'third@email.com');

INSERT INTO items (name, description, is_available, owner_id, request_id)
          VALUES
              ('First item', 'Without description', true, 1, NULL),
              ('Second item', 'To long description', true, 2, NULL),
              ('Third item', 'Another description', true, 1, NULL),
              ('Forth item', 'Non-available item', false, 1, NULL);