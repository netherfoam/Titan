--
-- These items are not in our cache, and therefore don't belong in our database.
--

DELETE FROM Equipment WHERE item_id IN (
    14694, 14695, 20051
);

DELETE FROM Item WHERE id IN (
    14694, 14695, 20051
);