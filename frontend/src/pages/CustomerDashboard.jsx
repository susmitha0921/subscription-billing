import { useState, useEffect } from 'react';
import axios from 'axios';

export default function CustomerDashboard() {
  const [plans, setPlans] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  const [message, setMessage] = useState('');

  const fetchData = async () => {
    try {
      const [planRes, subRes] = await Promise.all([
        axios.get('/api/plans'),
        axios.get('/api/subscriptions')
      ]);
      setPlans(planRes.data);
      setSubscriptions(subRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubscribe = async (planId) => {
    try {
      await axios.post('/api/subscriptions', { planId });
      setMessage('Successfully subscribed!');
      fetchData();
    } catch (err) {
      setMessage('Error subscribing: ' + (err.response?.data || err.message));
    }
  };

  const handleCancel = async (subId) => {
    try {
      await axios.put(`/api/subscriptions/${subId}/cancel`);
      setMessage('Subscription cancelled (effective end of period)');
      fetchData();
    } catch (err) {
      setMessage('Error cancelling: ' + (err.response?.data || err.message));
    }
  };

  const handleUpgradeDowngrade = async (subId, newPlanId, isUpgrade) => {
    const endpoint = isUpgrade ? 'upgrade' : 'downgrade';
    try {
      await axios.put(`/api/subscriptions/${subId}/${endpoint}`, { planId: newPlanId });
      setMessage(`Successfully requested ${endpoint}`);
      fetchData();
    } catch (err) {
      setMessage(`Error on ${endpoint}: ` + (err.response?.data || err.message));
    }
  };

  const handlePayment = async (invoiceId) => {
    try {
      const res = await axios.post('/api/payments/process', { 
        invoiceId, 
        paymentMethod: 'CARD' // Hardcoded for this simple UI
      });
      if (res.data.status === 'SUCCESS') {
        setMessage('Payment successful!');
      } else {
        setMessage('Payment failed! Reason: ' + res.data.failureReason);
      }
      fetchData(); // Refresh to get updated invoice and subscription status
    } catch (err) {
      setMessage('Error processing payment: ' + (err.response?.data || err.message));
    }
  };

  return (
    <div>
      <h1>Customer Dashboard</h1>
      
      {message && <div style={{ padding: '10px', backgroundColor: '#e2f0d9', marginBottom: '20px', border: '1px solid #70ad47' }}>{message}</div>}

      <div className="card">
        <h2>Your Subscriptions</h2>
        {subscriptions.length === 0 ? <p>You have no active subscriptions.</p> : (
          <table>
            <thead>
              <tr>
                <th>Plan</th>
                <th>Status</th>
                <th>Next Billing</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {subscriptions.map(s => (
                <tr key={s.id}>
                  <td>{s.plan.name}</td>
                  <td>{s.status} {s.cancelAtPeriodEnd ? '(Cancels at Period End)' : ''}</td>
                  <td>{new Date(s.nextBillingDate).toLocaleDateString()}</td>
                  <td>
                    {!s.cancelAtPeriodEnd && (
                        <button style={{ backgroundColor: 'red', marginRight: '5px' }} onClick={() => handleCancel(s.id)}>Cancel</button>
                    )}
                    {/* Basic upgrade/downgrade logic for display purposes */}
                    <select onChange={(e) => {
                      if(e.target.value !== "") {
                         const newPlanId = parseInt(e.target.value);
                         const newPlan = plans.find(p => p.id === newPlanId);
                         const isUpgrade = newPlan.price > s.plan.price;
                         handleUpgradeDowngrade(s.id, newPlanId, isUpgrade);
                      }
                    }}>
                      <option value="">Change Plan...</option>
                      {plans.filter(p => p.id !== s.plan.id).map(p => (
                        <option key={p.id} value={p.id}>{p.name} (${p.price})</option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="card">
        <h2>Pending Invoices</h2>
        {/* We would normally fetch invoices, let's make a separate component or inline fetch */}
        <InvoicesList subscriptions={subscriptions} onPayment={handlePayment} />
      </div>

      <div className="card">
        <h2>Available Plans</h2>
        <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
          {plans.map(p => (
            <div key={p.id} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '5px', minWidth: '200px' }}>
              <h3>{p.name}</h3>
              <p>{p.description}</p>
              <p><strong>${p.price} / {p.billingCycle}</strong></p>
              <button onClick={() => handleSubscribe(p.id)}>Subscribe</button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// Helper component to fetch and show invoices for the customer
function InvoicesList({ subscriptions, onPayment }) {
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    const fetchInvoices = async () => {
      try {
        let allInvs = [];
        for (const sub of subscriptions) {
          const res = await axios.get(`/api/subscriptions/${sub.id}/invoices`);
          allInvs = [...allInvs, ...res.data];
        }
        setInvoices(allInvs);
      } catch (err) {
        console.error("Error fetching invoices", err);
      }
    };
    if (subscriptions.length > 0) {
      fetchInvoices();
    }
  }, [subscriptions]);

  const pendingInvoices = invoices.filter(i => i.status === 'PENDING');

  if (pendingInvoices.length === 0) return <p>No pending invoices.</p>;

  return (
    <table>
      <thead>
        <tr>
          <th>Invoice #</th>
          <th>Plan</th>
          <th>Amount</th>
          <th>Due Date</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        {pendingInvoices.map(inv => (
          <tr key={inv.id}>
            <td>{inv.invoiceNumber}</td>
            <td>{inv.subscription?.plan?.name}</td>
            <td>${inv.amount}</td>
            <td>{new Date(inv.dueDate).toLocaleDateString()}</td>
            <td>
              <button style={{ backgroundColor: '#28a745' }} onClick={() => onPayment(inv.id)}>Pay Now</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
