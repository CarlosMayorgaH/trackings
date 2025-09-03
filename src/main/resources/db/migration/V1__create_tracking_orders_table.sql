-- Flyway migration script to create tracking_orders table
-- Version: V1
-- Description: Create tracking_orders table

-- Create tracking_orders table
CREATE TABLE tracking_orders (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(100) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    destination VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    estimated_delivery TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_status CHECK (status IN (
        'PENDING', 
        'IN_TRANSIT', 
        'OUT_FOR_DELIVERY', 
        'DELIVERED', 
        'FAILED', 
        'CANCELLED'
    ))
);

-- Create indexes for better query performance
CREATE INDEX idx_tracking_orders_tracking_number ON tracking_orders(tracking_number);
CREATE INDEX idx_tracking_orders_status ON tracking_orders(status);
CREATE INDEX idx_tracking_orders_customer_name ON tracking_orders(customer_name);
CREATE INDEX idx_tracking_orders_created_at ON tracking_orders(created_at DESC);
CREATE INDEX idx_tracking_orders_updated_at ON tracking_orders(updated_at DESC);

-- Insert sample data for testing
INSERT INTO tracking_orders (
    tracking_number, 
    customer_name, 
    destination, 
    status, 
    created_at, 
    updated_at, 
    estimated_delivery
) VALUES 
('TRK001', 'John Doe', '123 Main St, New York, NY 10001', 'PENDING', 
 CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', 
 CURRENT_TIMESTAMP + INTERVAL '3 days'),
 
('TRK002', 'Jane Smith', '456 Oak Ave, Los Angeles, CA 90001', 'IN_TRANSIT', 
 CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '4 hours', 
 CURRENT_TIMESTAMP + INTERVAL '2 days'),
 
('TRK003', 'Bob Johnson', '789 Pine St, Chicago, IL 60601', 'OUT_FOR_DELIVERY', 
 CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 hour', 
 CURRENT_TIMESTAMP + INTERVAL '1 day'),
 
('TRK004', 'Alice Brown', '321 Elm St, Houston, TX 77001', 'DELIVERED', 
 CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP - INTERVAL '2 days', 
 CURRENT_TIMESTAMP - INTERVAL '2 days'),
 
('TRK005', 'Charlie Wilson', '654 Maple Dr, Phoenix, AZ 85001', 'FAILED', 
 CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 
 CURRENT_TIMESTAMP - INTERVAL '1 day'),

('TRK006', 'Diana Davis', '987 Cedar Ln, Philadelphia, PA 19101', 'CANCELLED', 
 CURRENT_TIMESTAMP - INTERVAL '2 weeks', CURRENT_TIMESTAMP - INTERVAL '1 week', 
 NULL);

-- Add comments for documentation
COMMENT ON TABLE tracking_orders IS 'Table storing tracking order information';
COMMENT ON COLUMN tracking_orders.id IS 'Primary key, auto-incrementing';
COMMENT ON COLUMN tracking_orders.tracking_number IS 'Unique tracking identifier';
COMMENT ON COLUMN tracking_orders.customer_name IS 'Name of the customer';
COMMENT ON COLUMN tracking_orders.destination IS 'Delivery destination address';
COMMENT ON COLUMN tracking_orders.status IS 'Current status of the tracking order';
COMMENT ON COLUMN tracking_orders.created_at IS 'Timestamp when the order was created';
COMMENT ON COLUMN tracking_orders.updated_at IS 'Timestamp when the order was last updated';
COMMENT ON COLUMN tracking_orders.estimated_delivery IS 'Estimated delivery date and time';