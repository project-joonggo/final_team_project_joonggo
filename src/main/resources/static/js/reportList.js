console.log("reportList.js in");
function updateStatus(selectElement) {
    const reportId = selectElement.getAttribute('data-report-id');
    const newStatus = selectElement.value;

    fetch(`/user/admin/updateReportStatus`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            reportId: reportId,
            status: newStatus
        }),
    })
        .then(response => {
            if (response.ok) {
                alert('상태가 갱신되었습니다.');
            } else {
                alert('Failed to update status.');
            }
        })
        .catch(error => {
            console.error('Error updating status:', error);
            alert('An error occurred.');
        });
}